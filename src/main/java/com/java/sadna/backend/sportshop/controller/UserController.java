package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.authusers.api.UsersApi;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.ChangePasswordRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UpdateProfileRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponse;
import com.java.sadna.backend.sportshop.mapper.UserDtoToUserResponseMapper;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class UserController implements UsersApi {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserDtoToUserResponseMapper userDtoToUserResponseMapper;
    private final HttpServletResponse httpServletResponse;

    public UserController(UserService userService,
                          UserDtoToUserResponseMapper userDtoToUserResponseMapper,
                          HttpServletResponse httpServletResponse) {
        this.userService = userService;
        this.userDtoToUserResponseMapper = userDtoToUserResponseMapper;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateProfile(UpdateProfileRequest updateProfileRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(userService.updateProfile(userId, updateProfileRequest))
        );
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(ChangePasswordRequest changePasswordRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAccount(String xConfirmPassword) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        userService.deleteAccount(userId, xConfirmPassword, httpServletResponse);
        // Fire-and-forget cleanup; only reached if deleteAccount committed, so a thrown
        // delete never triggers orphan cleanup. Failures are logged + swallowed.
        CompletableFuture.runAsync(() -> {
            try {
                userService.cleanupDeletedUser(userId);
            } catch (RuntimeException e) {
                log.warn("Cleanup after user deletion failed for userId={}: {}", userId, e.toString(), e);
            }
        });
        return ResponseEntity.noContent().build();
    }
}
