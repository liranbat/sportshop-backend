package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.LoginRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.RegisterRequest;
import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import com.java.sadna.backend.sportshop.mapper.UserEntityToUserDtoMapper;
import com.java.sadna.backend.sportshop.model.UserDto;
import com.java.sadna.backend.sportshop.repository.RefreshTokenRepository;
import com.java.sadna.backend.sportshop.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password.";
    private static final String INVALID_REFRESH_MESSAGE = "Refresh token is invalid or expired.";
    private static final String EMAIL_TAKEN_MESSAGE = "An account with this email already exists.";

    // 32 bytes = 256 bits of entropy -- far beyond what's practical to guess
    // even with the unbounded validity window between issuance and rotation.
    private static final int REFRESH_TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final UserEntityToUserDtoMapper userEntityToUserDtoMapper;
    private final Duration refreshTokenTtl;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       CookieService cookieService,
                       UserEntityToUserDtoMapper userEntityToUserDtoMapper,
                       AppProperties appProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.userEntityToUserDtoMapper = userEntityToUserDtoMapper;
        this.refreshTokenTtl = appProperties.getAuth().getRefreshTokenTtl();
    }

    @Transactional
    public UserDto register(RegisterRequest dto) {
        String email = normalizeEmail(dto.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(EMAIL_TAKEN_MESSAGE);
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
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE));
        if (!passwordEncoder.matches(dto.getPassword(), entity.getPasswordHash())) {
            throw new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE);
        }
        issueSession(entity, response);
        return userEntityToUserDtoMapper.map(entity);
    }

    @Transactional
    public void logout(Long userIdOrNull, HttpServletResponse response) {
        if (userIdOrNull != null) {
            refreshTokenRepository.deleteByUserId(userIdOrNull);
        }
        attachClearedCookies(response);
    }

    @Transactional
    public UserDto refresh(HttpServletRequest request, HttpServletResponse response) {
        // Missing / unknown / expired all surface the same message so an attacker
        // probing /auth/refresh cannot tell whether a cookie was even present.
        String refreshTokenValue = cookieService.readRefreshCookie(request)
                .orElseThrow(() -> new UnauthorizedException(INVALID_REFRESH_MESSAGE));
        RefreshTokenEntity row = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException(INVALID_REFRESH_MESSAGE));
        if (row.getExpiresAt().isBefore(OffsetDateTime.now())) {
            // Drop the dead row eagerly so /auth/refresh stops finding it on
            // subsequent retries and the user is forced through full login.
            refreshTokenRepository.delete(row);
            throw new UnauthorizedException(INVALID_REFRESH_MESSAGE);
        }
        UserEntity user = userRepository.findByIdAndDeletedFalse(row.getUserId())
                .orElseThrow(() -> new UnauthorizedException(INVALID_REFRESH_MESSAGE));

        String newRefreshToken = generateRefreshTokenValue();
        OffsetDateTime newExpiresAt = OffsetDateTime.now().plus(refreshTokenTtl);
        
        // Mutated in place; JPA dirty-checking inside @Transactional flushes the UPDATE.
        row.rotate(newRefreshToken, newExpiresAt);

        String accessToken = jwtService.issueAccessToken(user.getId(), user.isAdmin());
        attachSessionCookies(response, accessToken, newRefreshToken);
        return userEntityToUserDtoMapper.map(user);
    }

    @Transactional(readOnly = true)
    public UserDto getMe(Long userId) {
        UserEntity entity = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException("Session is no longer valid."));
        return userEntityToUserDtoMapper.map(entity);
    }

    private void issueSession(UserEntity entity, HttpServletResponse response) {
        String refreshToken = generateRefreshTokenValue();
        OffsetDateTime expiresAt = OffsetDateTime.now().plus(refreshTokenTtl);
        upsertRefreshTokenRow(entity.getId(), refreshToken, expiresAt);
        String accessToken = jwtService.issueAccessToken(entity.getId(), entity.isAdmin());
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

    private void attachClearedCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieService.clearAccessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieService.clearRefreshCookie().toString());
    }

    private String generateRefreshTokenValue() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase();
    }
}
