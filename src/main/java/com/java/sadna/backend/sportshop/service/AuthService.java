package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.LoginRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.RegisterRequest;
import com.java.sadna.backend.sportshop.common.util.SortDirections;
import com.java.sadna.backend.sportshop.common.util.SortResolver;
import com.java.sadna.backend.sportshop.config.AuthProperties;
import com.java.sadna.backend.sportshop.config.PaginationProperties;
import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import com.java.sadna.backend.sportshop.mapper.RefreshTokenEntityToSessionDtoMapper;
import com.java.sadna.backend.sportshop.mapper.UserEntityToUserDtoMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.SessionDto;
import com.java.sadna.backend.sportshop.model.UserDto;
import com.java.sadna.backend.sportshop.repository.RefreshTokenRepository;
import com.java.sadna.backend.sportshop.repository.UserRepository;
import com.java.sadna.backend.sportshop.repository.specification.SessionSpecifications;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private static final SortResolver SESSION_SORT_RESOLVER = new SortResolver(
            Map.of(
                    "user", List.of("user.email"),
                    "expiresAt", List.of("expiresAt")
            ),
            SortResolver.orders("expiresAt", SortDirections.DESC, "id", SortDirections.ASC),
            SortResolver.orders("id", SortDirections.ASC)
    );

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final PaginationService paginationService;
    private final UserEntityToUserDtoMapper userEntityToUserDtoMapper;
    private final RefreshTokenEntityToSessionDtoMapper refreshTokenEntityToSessionDtoMapper;
    private final SecureRandom secureRandom;
    private final Duration refreshTokenTtl;
    private final int refreshTokenBytes;
    private final int defaultSessionPageSize;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       CookieService cookieService,
                       PaginationService paginationService,
                       UserEntityToUserDtoMapper userEntityToUserDtoMapper,
                       RefreshTokenEntityToSessionDtoMapper refreshTokenEntityToSessionDtoMapper,
                       SecureRandom secureRandom,
                       AuthProperties authProperties,
                       PaginationProperties paginationProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.paginationService = paginationService;
        this.userEntityToUserDtoMapper = userEntityToUserDtoMapper;
        this.refreshTokenEntityToSessionDtoMapper = refreshTokenEntityToSessionDtoMapper;
        this.secureRandom = secureRandom;
        this.refreshTokenTtl = authProperties.getRefreshTokenTtl();
        this.refreshTokenBytes = authProperties.getRefreshTokenBytes();
        this.defaultSessionPageSize = paginationProperties.getDefaultPageSize()
                .getOrDefault("sessions", 20);
    }

    @Transactional
    public UserDto register(RegisterRequest dto) {
        String email = normalizeEmail(dto.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("auth.emailTaken");
        }
        UserEntity entity = new UserEntity(
                dto.getFirstName(),
                dto.getLastName(),
                email,
                dto.getPhone(),
                passwordEncoder.encode(dto.getPassword()),
                false
        );
        UserEntity saved = userRepository.save(entity);
        return userEntityToUserDtoMapper.map(saved);
    }

    @Transactional
    public UserDto login(LoginRequest dto, HttpServletResponse response) {
        UserEntity entity = userRepository.findByEmailAndDeletedFalse(normalizeEmail(dto.getEmail()))
                .orElseThrow(() -> new UnauthorizedException("auth.invalidCredentials"));
        if (!passwordEncoder.matches(dto.getPassword(), entity.getPasswordHash())) {
            throw new UnauthorizedException("auth.invalidCredentials");
        }
        issueSession(entity, response);
        return userEntityToUserDtoMapper.map(entity);
    }

    @Transactional
    public void logout(Long userIdOrNull, HttpServletResponse response) {
        if (userIdOrNull != null) {
            refreshTokenRepository.deleteByUserId(userIdOrNull);
        }
        cookieService.clearAuthCookies(response);
    }

    @Transactional
    public UserDto refresh(HttpServletRequest request, HttpServletResponse response) {
        // Missing / unknown / expired all surface the same message so an attacker
        // probing /auth/refresh cannot tell whether a cookie was even present.
        String refreshTokenValue = cookieService.readRefreshCookie(request)
                .orElseThrow(() -> new UnauthorizedException("auth.invalidRefresh"));
        RefreshTokenEntity row = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("auth.invalidRefresh"));
        if (row.getExpiresAt().isBefore(OffsetDateTime.now())) {
            // Drop the dead row eagerly so /auth/refresh stops finding it on
            // subsequent retries and the user is forced through full login.
            refreshTokenRepository.delete(row);
            throw new UnauthorizedException("auth.invalidRefresh");
        }
        UserEntity user = userRepository.findByIdAndDeletedFalse(row.getUserId())
                .orElseThrow(() -> new UnauthorizedException("auth.invalidRefresh"));

        String newRefreshToken = generateRefreshTokenValue();
        OffsetDateTime newExpiresAt = OffsetDateTime.now().plus(refreshTokenTtl);
        
        // Mutated in place; JPA dirty-checking inside @Transactional flushes the UPDATE.
        row.rotate(newRefreshToken, newExpiresAt);

        String accessToken = jwtService.issueAccessToken(user.getId());
        attachSessionCookies(response, accessToken, newRefreshToken);
        return userEntityToUserDtoMapper.map(user);
    }

    @Transactional(readOnly = true)
    public UserDto getMe(Long userId) {
        UserEntity entity = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException("auth.session.invalidSession"));
        return userEntityToUserDtoMapper.map(entity);
    }

    @Transactional(readOnly = true)
    public PagedResult<SessionDto> listSessions(String q,
                                                String sortField,
                                                String sortDirection,
                                                Integer page,
                                                Integer pageSize) {
        Specification<RefreshTokenEntity> spec = Specification.allOf(
                SessionSpecifications.searchMatches(q)
        );
        Sort sort = SESSION_SORT_RESOLVER.resolve(sortField, sortDirection);
        return paginationService.paginate(
                refreshTokenRepository, spec, sort, page, pageSize, defaultSessionPageSize,
                refreshTokenEntityToSessionDtoMapper
        );
    }

    @Transactional
    public void revokeSession(Long sessionId, Long actorId) {
        int deleted = refreshTokenRepository.deleteByIdExcludingActor(sessionId, actorId);
        if (deleted == 0) {
            throw new ConflictException("auth.session.revokeConflict");
        }
    }

    @Transactional
    public int revokeAllSessionsExceptActor(Long actorId) {
        return refreshTokenRepository.deleteAllExceptActor(actorId);
    }

    private void issueSession(UserEntity entity, HttpServletResponse response) {
        String refreshToken = generateRefreshTokenValue();
        OffsetDateTime expiresAt = OffsetDateTime.now().plus(refreshTokenTtl);
        upsertRefreshTokenRow(entity.getId(), refreshToken, expiresAt);
        String accessToken = jwtService.issueAccessToken(entity.getId());
        attachSessionCookies(response, accessToken, refreshToken);
    }

    private void upsertRefreshTokenRow(Long userId, String token, OffsetDateTime expiresAt) {
        // find-then-mutate-or-insert (instead of a native ON CONFLICT) keeps
        // the entity JPA-managed and lets dirty checking handle the UPDATE.
        // The UNIQUE(user_id) constraint in V4 still backstops any race.
        refreshTokenRepository.findByUserId(userId).ifPresentOrElse(
                existing -> existing.rotate(token, expiresAt),
                () -> refreshTokenRepository.save(new RefreshTokenEntity(userId, token, expiresAt))
        );
    }

    private void attachSessionCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie access = cookieService.buildAccessCookie(accessToken);
        ResponseCookie refresh = cookieService.buildRefreshCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

    private String generateRefreshTokenValue() {
        byte[] bytes = new byte[refreshTokenBytes];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase();
    }
}
