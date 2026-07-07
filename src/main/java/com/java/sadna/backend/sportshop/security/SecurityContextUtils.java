package com.java.sadna.backend.sportshop.security;

import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityContextUtils {

    private SecurityContextUtils() {
    }

    public static Optional<Long> currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Optional.empty();
        }
        return auth.getPrincipal() instanceof Long userId ? Optional.of(userId) : Optional.empty();
    }

    public static Long currentUserIdOrThrow() {
        return currentUserId().orElseThrow(UnauthorizedException::new);
    }

    public static boolean currentUserIsAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(Role.ADMIN.authority()::equals);
    }
}
