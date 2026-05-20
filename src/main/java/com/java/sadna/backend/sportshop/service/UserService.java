package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.ChangePasswordRequestDto;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UpdateProfileRequestDto;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import com.java.sadna.backend.sportshop.mapper.UserEntityToUserMapper;
import com.java.sadna.backend.sportshop.model.User;
import com.java.sadna.backend.sportshop.repository.RefreshTokenRepository;
import com.java.sadna.backend.sportshop.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private static final String USER_NOT_ACTIVE_MESSAGE = "User does not exist or is not active.";
    private static final String CURRENT_PASSWORD_INCORRECT_MESSAGE = "Current password is incorrect.";
    private static final String CONCURRENT_MODIFICATION_MESSAGE =
            "Your account was modified elsewhere. Please refresh and try again.";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final UserEntityToUserMapper userEntityToUserMapper;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       CookieService cookieService,
                       UserEntityToUserMapper userEntityToUserMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.cookieService = cookieService;
        this.userEntityToUserMapper = userEntityToUserMapper;
    }

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequestDto dto) {
        int updated = userRepository.applyProfileEdit(
                userId,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhone(),
                userId,
                OffsetDateTime.now()
        );
        
        if (updated == 0) {
            throw new UnauthorizedException(USER_NOT_ACTIVE_MESSAGE);
        }

        UserEntity fresh = loadActiveOrThrow(userId);
        return userEntityToUserMapper.map(fresh);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto dto) {
        UserEntity entity = loadActiveOrThrow(userId);
        if (!passwordEncoder.matches(dto.getCurrentPassword(), entity.getPasswordHash())) {
            throw new UnauthorizedException(CURRENT_PASSWORD_INCORRECT_MESSAGE);
        }

        int updated = userRepository.rotatePassword(
                userId,
                entity.getPasswordHash(),
                passwordEncoder.encode(dto.getNewPassword()),
                userId,
                OffsetDateTime.now()
        );

        if (updated == 0) {
            throw new ConflictException(CONCURRENT_MODIFICATION_MESSAGE);
        }
    }

    @Transactional
    public void deleteAccount(Long userId, String currentPassword, HttpServletResponse response) {
        UserEntity entity = loadActiveOrThrow(userId);
        if (!passwordEncoder.matches(currentPassword, entity.getPasswordHash())) {
            throw new UnauthorizedException(CURRENT_PASSWORD_INCORRECT_MESSAGE);
        }
        
        int deleted = userRepository.softDelete(
                userId,
                entity.getPasswordHash(),
                OffsetDateTime.now()
        );

        if (deleted == 0) {
            throw new ConflictException(CONCURRENT_MODIFICATION_MESSAGE);
        }
        // Order matters: drop refresh tokens first so any in-flight refresh on this
        // user 401s immediately; the row stays soft-deleted but the session is gone.
        refreshTokenRepository.deleteByUserId(userId);
        // TODO: when the cart slice lands, hard-delete this user's cart_items here
        // inside the same @Transactional boundary (project-summary §3.10).
        attachClearedCookies(response);
    }

    private UserEntity loadActiveOrThrow(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException(USER_NOT_ACTIVE_MESSAGE));
    }

    private void attachClearedCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieService.clearAccessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieService.clearRefreshCookie().toString());
    }
}
