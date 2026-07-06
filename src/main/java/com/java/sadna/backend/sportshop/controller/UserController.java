package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.authusers.api.AdminUsersApi;
import com.java.sadna.backend.sportshop.api.generated.authusers.api.UsersApi;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.ChangePasswordRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UpdateProfileRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserListPage;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponse;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserRoleFilter;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserStatusFilter;
import com.java.sadna.backend.sportshop.mapper.PagedUserDtoToUserListPageMapper;
import com.java.sadna.backend.sportshop.mapper.UserDtoToUserResponseMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.UserDto;
import com.java.sadna.backend.sportshop.security.JwtCookieAuthenticationFilter;
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
public class UserController implements UsersApi, AdminUsersApi {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserDtoToUserResponseMapper userDtoToUserResponseMapper;
    private final PagedUserDtoToUserListPageMapper pagedUserDtoToUserListPageMapper;
    private final HttpServletResponse httpServletResponse;

    public UserController(UserService userService,
                          UserDtoToUserResponseMapper userDtoToUserResponseMapper,
                          PagedUserDtoToUserListPageMapper pagedUserDtoToUserListPageMapper,
                          HttpServletResponse httpServletResponse) {
        this.userService = userService;
        this.userDtoToUserResponseMapper = userDtoToUserResponseMapper;
        this.pagedUserDtoToUserListPageMapper = pagedUserDtoToUserListPageMapper;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateProfile(UpdateProfileRequest updateProfileRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(
                        userService.updateProfile(userId, userId, false, updateProfileRequest)
                )
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
        scheduleUserCleanupAfterCommit(userId, "user deletion");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListPage> listAdminUsers(UserRoleFilter role,
                                                       UserStatusFilter status,
                                                       String q,
                                                       String sortField,
                                                       String sortDirection,
                                                       Integer page,
                                                       Integer pageSize) {
        Boolean isAdmin = toIsAdmin(role);
        Boolean isDeleted = toIsDeleted(status);

        PagedResult<UserDto> result = userService.listUsers(
                isAdmin, isDeleted, q, sortField, sortDirection, page, pageSize
        );
        return ResponseEntity.ok(pagedUserDtoToUserListPageMapper.map(result));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getAdminUserById(Long id) {
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(userService.getUserByIdAsAdmin(id))
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateAdminUser(Long id, UpdateProfileRequest updateProfileRequest) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(
                        userService.updateProfile(id, actorId, true, updateProfileRequest)
                )
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> promoteAdminUser(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(userService.promoteToAdmin(id, actorId))
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> demoteAdminUser(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        UserDto updated = userService.demoteFromAdmin(id, actorId);
        // self-demote: filter set X-Auth-Role: admin pre-update, override so the
        // frontend's auth-interceptor flips me.isAdmin on this very response
        if (id.equals(actorId)) {
            httpServletResponse.setHeader(
                    JwtCookieAuthenticationFilter.ROLE_HEADER,
                    JwtCookieAuthenticationFilter.ROLE_USER_VALUE
            );
        }
        return ResponseEntity.ok(userDtoToUserResponseMapper.map(updated));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> softDeleteAdminUser(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        UserDto updated = userService.softDeleteAsAdmin(id, actorId);
        scheduleUserCleanupAfterCommit(id, "admin soft-delete");
        return ResponseEntity.ok(userDtoToUserResponseMapper.map(updated));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> restoreAdminUser(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(userService.restoreAsAdmin(id, actorId))
        );
    }

    private void scheduleUserCleanupAfterCommit(Long userId, String source) {
        CompletableFuture.runAsync(() -> {
            try {
                userService.cleanupDeletedUser(userId);
            } catch (RuntimeException e) {
                log.warn("Cleanup after {} failed for userId={}: {}", source, userId, e.toString(), e);
            }
        });
    }

    private static Boolean toIsAdmin(UserRoleFilter role) {
        if (role == null) return null;
        return switch (role) {
            case ADMIN -> Boolean.TRUE;
            case USER -> Boolean.FALSE;
        };
    }

    private static Boolean toIsDeleted(UserStatusFilter status) {
        if (status == null) return null;
        return switch (status) {
            case ACTIVE -> Boolean.FALSE;
            case DELETED -> Boolean.TRUE;
        };
    }
}
