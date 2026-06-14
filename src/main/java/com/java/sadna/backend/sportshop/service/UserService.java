package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.ChangePasswordRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UpdateProfileRequest;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import com.java.sadna.backend.sportshop.mapper.UserEntityToUserDtoMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.UserDto;
import com.java.sadna.backend.sportshop.repository.CartItemRepository;
import com.java.sadna.backend.sportshop.repository.RefreshTokenRepository;
import com.java.sadna.backend.sportshop.repository.UserRepository;
import com.java.sadna.backend.sportshop.repository.specification.UserSpecifications;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private static final String USER_NOT_ACTIVE_MESSAGE = "User does not exist or is not active.";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";
    private static final String CURRENT_PASSWORD_INCORRECT_MESSAGE = "Current password is incorrect.";
    private static final String CONCURRENT_MODIFICATION_MESSAGE =
            "Your account was modified elsewhere. Please refresh and try again.";

    private static final String SORT_FIELD_NAME = "name";
    private static final String SORT_FIELD_EMAIL = "email";
    private static final String SORT_PATH_ID = "id";
    private static final String SORT_PATH_FIRST_NAME = "firstName";
    private static final String SORT_PATH_LAST_NAME = "lastName";
    private static final String SORT_PATH_EMAIL = "email";
    private static final String SORT_DIRECTION_ASC = "asc";
    private static final String SORT_DIRECTION_DESC = "desc";

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CartItemRepository cartItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final PaginationService paginationService;
    private final UserEntityToUserDtoMapper userEntityToUserDtoMapper;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       CartItemRepository cartItemRepository,
                       PasswordEncoder passwordEncoder,
                       CookieService cookieService,
                       PaginationService paginationService,
                       UserEntityToUserDtoMapper userEntityToUserDtoMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.cartItemRepository = cartItemRepository;
        this.passwordEncoder = passwordEncoder;
        this.cookieService = cookieService;
        this.paginationService = paginationService;
        this.userEntityToUserDtoMapper = userEntityToUserDtoMapper;
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
                throw new NotFoundException(USER_NOT_FOUND_MESSAGE);
            }
            throw new UnauthorizedException(USER_NOT_ACTIVE_MESSAGE);
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
        attachClearedCookies(response);
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

        Sort sort = buildSort(sortField, sortDirection);

        return paginationService.paginate(
                userRepository, spec, sort, page, pageSize, DEFAULT_PAGE_SIZE,
                userEntityToUserDtoMapper
        );
    }

    private UserEntity loadActiveOrThrow(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException(USER_NOT_ACTIVE_MESSAGE));
    }

    private UserEntity loadByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    private void attachClearedCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieService.clearAccessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieService.clearRefreshCookie().toString());
    }

    private Sort buildSort(String sortField, String sortDirection) {
        // Default: id desc, no extra tiebreaker (id is already unique).
        if (sortField == null || sortField.isBlank()) {
            return Sort.by(Sort.Order.desc(SORT_PATH_ID));
        }
        if (SORT_FIELD_NAME.equalsIgnoreCase(sortField)) {
            Sort.Direction dir = directionFor(sortDirection, Sort.Direction.ASC);
            return Sort.by(
                    new Sort.Order(dir, SORT_PATH_FIRST_NAME),
                    new Sort.Order(dir, SORT_PATH_LAST_NAME),
                    Sort.Order.asc(SORT_PATH_ID)
            );
        }
        if (SORT_FIELD_EMAIL.equalsIgnoreCase(sortField)) {
            Sort.Direction dir = directionFor(sortDirection, Sort.Direction.ASC);
            return Sort.by(new Sort.Order(dir, SORT_PATH_EMAIL), Sort.Order.asc(SORT_PATH_ID));
        }
        // sortField=id (or unknown) -> sort by id with default desc.
        Sort.Direction dir = directionFor(sortDirection, Sort.Direction.DESC);
        return Sort.by(new Sort.Order(dir, SORT_PATH_ID));
    }

    private Sort.Direction directionFor(String sortDirection, Sort.Direction fieldDefault) {
        if (sortDirection == null || sortDirection.isBlank()) return fieldDefault;
        if (SORT_DIRECTION_ASC.equalsIgnoreCase(sortDirection)) return Sort.Direction.ASC;
        if (SORT_DIRECTION_DESC.equalsIgnoreCase(sortDirection)) return Sort.Direction.DESC;
        return fieldDefault;
    }
}
