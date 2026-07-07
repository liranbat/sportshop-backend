package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.ChangePasswordRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UpdateProfileRequest;
import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import com.java.sadna.backend.sportshop.common.constants.PageSizeConstants;
import com.java.sadna.backend.sportshop.common.constants.UserConstants;
import com.java.sadna.backend.sportshop.common.util.SortDirections;
import com.java.sadna.backend.sportshop.common.util.SortResolver;
import com.java.sadna.backend.sportshop.config.PaginationProperties;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import com.java.sadna.backend.sportshop.mapper.entity.dto.UserEntityToUserDtoMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.UserDto;
import com.java.sadna.backend.sportshop.repository.CartItemRepository;
import com.java.sadna.backend.sportshop.repository.RefreshTokenRepository;
import com.java.sadna.backend.sportshop.repository.UserRepository;
import com.java.sadna.backend.sportshop.repository.specification.UserSpecifications;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final SortResolver SORT_RESOLVER = new SortResolver(
            Map.of(
                    UserConstants.Sort.NAME, List.of(UserConstants.FIRST_NAME, UserConstants.LAST_NAME),
                    UserConstants.Sort.EMAIL, List.of(UserConstants.EMAIL),
                    UserConstants.Sort.ID, List.of(UserConstants.ID)
            ),
            SortResolver.orders(UserConstants.ID, SortDirections.DESC),
            SortResolver.orders(UserConstants.ID, SortDirections.ASC)
    );

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CartItemRepository cartItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final PaginationService paginationService;
    private final UserEntityToUserDtoMapper userEntityToUserDtoMapper;
    private final int defaultPageSize;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       CartItemRepository cartItemRepository,
                       PasswordEncoder passwordEncoder,
                       CookieService cookieService,
                       PaginationService paginationService,
                       UserEntityToUserDtoMapper userEntityToUserDtoMapper,
                       PaginationProperties paginationProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.cartItemRepository = cartItemRepository;
        this.passwordEncoder = passwordEncoder;
        this.cookieService = cookieService;
        this.paginationService = paginationService;
        this.userEntityToUserDtoMapper = userEntityToUserDtoMapper;
        this.defaultPageSize = paginationProperties.getDefaultPageSize()
                .getOrDefault(PageSizeConstants.USERS_KEY, PageSizeConstants.USERS_DEFAULT);
    }

    @Transactional
    public UserDto updateProfile(Long targetUserId, Long actorUserId, boolean actorIsAdmin, UpdateProfileRequest dto) {
        int updated = userRepository.applyProfileEdit(
                targetUserId,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhone(),
                actorUserId,
                OffsetDateTime.now(),
                actorIsAdmin
        );

        if (updated == 0) {
            if (actorIsAdmin) {
                throw new NotFoundException(ErrorConstants.User.NOT_FOUND);
            }
            throw new UnauthorizedException(ErrorConstants.User.NOT_ACTIVE);
        }

        UserEntity fresh = actorIsAdmin
                ? loadByIdOrThrow(targetUserId)
                : loadActiveOrThrow(targetUserId);
        return userEntityToUserDtoMapper.map(fresh);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest dto) {
        UserEntity entity = loadActiveOrThrow(userId);
        if (!passwordEncoder.matches(dto.getCurrentPassword(), entity.getPasswordHash())) {
            throw new UnauthorizedException(ErrorConstants.User.CURRENT_PASSWORD_INCORRECT);
        }

        int updated = userRepository.rotatePassword(
                userId,
                entity.getPasswordHash(),
                passwordEncoder.encode(dto.getNewPassword()),
                userId,
                OffsetDateTime.now()
        );

        if (updated == 0) {
            throw new ConflictException(ErrorConstants.User.CONCURRENT_MODIFICATION);
        }
    }

    @Transactional
    public void deleteAccount(Long userId, String currentPassword, HttpServletResponse response) {
        UserEntity entity = loadActiveOrThrow(userId);
        if (!passwordEncoder.matches(currentPassword, entity.getPasswordHash())) {
            throw new UnauthorizedException(ErrorConstants.User.CURRENT_PASSWORD_INCORRECT);
        }
        if (entity.isAdmin() && userRepository.countByAdminTrueAndDeletedFalse() <= 1) {
            throw new ConflictException(ErrorConstants.User.DELETE_CONFLICT);
        }

        int deleted = userRepository.softDelete(
                userId,
                entity.getPasswordHash(),
                OffsetDateTime.now()
        );

        if (deleted == 0) {
            throw new ConflictException(ErrorConstants.User.DELETE_CONFLICT);
        }
        // Order matters: drop refresh tokens first so any in-flight refresh on this
        // user 401s immediately; the row stays soft-deleted but the session is gone.
        refreshTokenRepository.deleteByUserId(userId);
        cookieService.clearAuthCookies(response);
    }

    // Post-deletion cleanup. Caller fires this off the request thread; only call after
    // deleteAccount commits.
    @Transactional
    public void cleanupDeletedUser(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByIdAsAdmin(Long targetUserId) {
        return userEntityToUserDtoMapper.map(loadByIdOrThrow(targetUserId));
    }

    @Transactional
    public UserDto promoteToAdmin(Long targetUserId, Long actorUserId) {
        int updated = userRepository.applyPromote(targetUserId, actorUserId, OffsetDateTime.now());
        if (updated == 0) {
            throw new ConflictException(ErrorConstants.User.PROMOTE_CONFLICT);
        }
        return userEntityToUserDtoMapper.map(loadByIdOrThrow(targetUserId));
    }

    @Transactional
    public UserDto demoteFromAdmin(Long targetUserId, Long actorUserId) {
        int updated = userRepository.applyDemote(targetUserId, actorUserId, OffsetDateTime.now());
        if (updated == 0) {
            throw new ConflictException(ErrorConstants.User.DEMOTE_CONFLICT);
        }
        return userEntityToUserDtoMapper.map(loadByIdOrThrow(targetUserId));
    }

    @Transactional
    public UserDto softDeleteAsAdmin(Long targetUserId, Long actorUserId) {
        UserEntity entity = loadByIdOrThrow(targetUserId);
        if (entity.isAdmin() && !entity.isDeleted()
                && userRepository.countByAdminTrueAndDeletedFalse() <= 1) {
            throw new ConflictException(ErrorConstants.User.ADMIN_LAST_ADMIN_DELETE);
        }

        int updated = userRepository.applyAdminSoftDelete(
                targetUserId, actorUserId, OffsetDateTime.now()
        );
        if (updated == 0) {
            throw new ConflictException(ErrorConstants.User.ADMIN_SOFT_DELETE_CONFLICT);
        }
        // refresh tokens dropped in-txn so the target's in-flight refreshes 401
        // immediately; cart cleanup is fire-and-forget at the controller (cleanupDeletedUser)
        refreshTokenRepository.deleteByUserId(targetUserId);
        return userEntityToUserDtoMapper.map(loadByIdOrThrow(targetUserId));
    }

    @Transactional
    public UserDto restoreAsAdmin(Long targetUserId, Long actorUserId) {
        int updated = userRepository.applyAdminRestore(
                targetUserId, actorUserId, OffsetDateTime.now()
        );
        if (updated == 0) {
            throw new ConflictException(ErrorConstants.User.ADMIN_RESTORE_CONFLICT);
        }
        return userEntityToUserDtoMapper.map(loadByIdOrThrow(targetUserId));
    }

    @Transactional(readOnly = true)
    public PagedResult<UserDto> listUsers(Boolean isAdmin,
                                          Boolean isDeleted,
                                          String q,
                                          String sortField,
                                          String sortDirection,
                                          Integer page,
                                          Integer pageSize) {
        Specification<UserEntity> spec = Specification.allOf(
                UserSpecifications.isAdminEquals(isAdmin),
                UserSpecifications.isDeletedEquals(isDeleted),
                UserSpecifications.searchMatches(q)
        );

        Sort sort = SORT_RESOLVER.resolve(sortField, sortDirection);

        return paginationService.paginate(
                userRepository, spec, sort, page, pageSize, defaultPageSize,
                userEntityToUserDtoMapper
        );
    }

    private UserEntity loadActiveOrThrow(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorConstants.User.NOT_ACTIVE));
    }

    private UserEntity loadByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorConstants.User.NOT_FOUND));
    }
}
