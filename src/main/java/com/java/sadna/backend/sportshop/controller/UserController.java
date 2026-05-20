package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.authusers.api.UsersApi;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.ChangePasswordRequestDto;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UpdateProfileRequestDto;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponseDto;
import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import com.java.sadna.backend.sportshop.mapper.UserToUserResponseDtoMapper;
import com.java.sadna.backend.sportshop.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserToUserResponseDtoMapper userToUserResponseDtoMapper;
    private final HttpServletResponse httpServletResponse;

    public UserController(UserService userService,
                          UserToUserResponseDtoMapper userToUserResponseDtoMapper,
                          HttpServletResponse httpServletResponse) {
        this.userService = userService;
        this.userToUserResponseDtoMapper = userToUserResponseDtoMapper;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateProfile(UpdateProfileRequestDto updateProfileRequestDto) {
        Long userId = currentUserIdOrThrow();
        return ResponseEntity.ok(
                userToUserResponseDtoMapper.map(userService.updateProfile(userId, updateProfileRequestDto))
        );
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(ChangePasswordRequestDto changePasswordRequestDto) {
        Long userId = currentUserIdOrThrow();
        userService.changePassword(userId, changePasswordRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAccount(String xConfirmPassword) {
        Long userId = currentUserIdOrThrow();
        userService.deleteAccount(userId, xConfirmPassword, httpServletResponse);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserIdOrThrow() {
        return currentUserId().orElseThrow(UnauthorizedException::new);
    }

    private Optional<Long> currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Optional.empty();
        }
        // Only JwtCookieAuthenticationFilter puts a Long principal in the SecurityContext;
        // the anonymous filter uses a String "anonymousUser".
        return auth.getPrincipal() instanceof Long userId ? Optional.of(userId) : Optional.empty();
    }
}
