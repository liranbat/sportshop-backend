package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.authusers.api.AdminUsersApi;
import com.java.sadna.backend.sportshop.api.generated.authusers.api.UsersApi;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.ChangePasswordRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UpdateProfileRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserListPage;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponse;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserRoleFilter;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserStatusFilter;
import com.java.sadna.backend.sportshop.mapper.dto.response.PagedUserDtoToUserListPageMapper;
import com.java.sadna.backend.sportshop.mapper.dto.response.UserDtoToUserResponseMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.common.constants.ApiHeaderConstants;
import com.java.sadna.backend.sportshop.model.UserDto;
import com.java.sadna.backend.sportshop.common.constants.AuthorityConstants;
import com.java.sadna.backend.sportshop.common.constants.AsyncConstants;
import com.java.sadna.backend.sportshop.security.Role;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@Slf4j
public class UserController implements UsersApi, AdminUsersApi {

    private final UserService userService;
    private final UserDtoToUserResponseMapper userDtoToUserResponseMapper;
    private final PagedUserDtoToUserListPageMapper pagedUserDtoToUserListPageMapper;
    private final HttpServletResponse httpServletResponse;
    private final Executor cleanupExecutor;

    public UserController(UserService userService,
                          UserDtoToUserResponseMapper userDtoToUserResponseMapper,
                          PagedUserDtoToUserListPageMapper pagedUserDtoToUserListPageMapper,
                          HttpServletResponse httpServletResponse,
                          @Qualifier(AsyncConstants.Executors.CLEANUP) Executor cleanupExecutor) {
        this.userService = userService;
        this.userDtoToUserResponseMapper = userDtoToUserResponseMapper;
        this.pagedUserDtoToUserListPageMapper = pagedUserDtoToUserListPageMapper;
        this.httpServletResponse = httpServletResponse;
        this.cleanupExecutor = cleanupExecutor;
    }

    @Override
    @PreAuthorize(AuthorityConstants.AUTHENTICATED)
    public ResponseEntity<UserResponse> updateProfile(UpdateProfileRequest updateProfileRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(
                        userService.updateProfile(userId, userId, false, updateProfileRequest)
                )
        );
    }

    @Override
    @PreAuthorize(AuthorityConstants.AUTHENTICATED)
    public ResponseEntity<Void> changePassword(ChangePasswordRequest changePasswordRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize(AuthorityConstants.AUTHENTICATED)
    public ResponseEntity<Void> deleteAccount(String xConfirmPassword) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        userService.deleteAccount(userId, xConfirmPassword, httpServletResponse);
        scheduleUserCleanupAfterCommit(userId, "user deletion");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
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
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<UserResponse> getAdminUserById(Long id) {
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(userService.getUserByIdAsAdmin(id))
        );
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<UserResponse> updateAdminUser(Long id, UpdateProfileRequest updateProfileRequest) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(
                        userService.updateProfile(id, actorId, true, updateProfileRequest)
                )
        );
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<UserResponse> promoteAdminUser(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(userService.promoteToAdmin(id, actorId))
        );
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<UserResponse> demoteAdminUser(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        UserDto updated = userService.demoteFromAdmin(id, actorId);
        // self-demote: filter set X-Auth-Role: admin pre-update, override so the
        // frontend's auth-interceptor flips me.isAdmin on this very response
        if (id.equals(actorId)) {
            httpServletResponse.setHeader(
                    ApiHeaderConstants.X_AUTH_ROLE,
                    Role.USER.headerValue()
            );
        }
        return ResponseEntity.ok(userDtoToUserResponseMapper.map(updated));
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<UserResponse> softDeleteAdminUser(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        UserDto updated = userService.softDeleteAsAdmin(id, actorId);
        scheduleUserCleanupAfterCommit(id, "admin soft-delete");
        return ResponseEntity.ok(userDtoToUserResponseMapper.map(updated));
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
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
        }, cleanupExecutor);
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
