package com.java.sadna.backend.sportshop.security;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.repository.UserRepository;
import com.java.sadna.backend.sportshop.service.CookieService;
import com.java.sadna.backend.sportshop.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Reads the access_token cookie, validates the JWT, then loads the user row to:
//   1. confirm the user still exists and isn't soft-deleted (closes the
//      "valid JWT but admin-deleted account" window),
//   2. derive the LIVE is_admin authority -- the JWT no longer carries an
//      isAdmin claim, so promotion/demotion takes effect on the next request
//      without rotating tokens,
//   3. echo the role back to the client via the X-Auth-Role response header
//      so the frontend can keep its cached `me` in sync without polling /me.
//
// NOT a @Component: instantiated by SecurityConfig.securityFilterChain so it
// only runs inside the Spring Security chain. If it were a bean, Spring Boot
// would also auto-register it as a generic servlet filter and we'd double-run.
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORITY_ADMIN = "ROLE_ADMIN";
    public static final String AUTHORITY_USER = "ROLE_USER";

    public static final String ROLE_HEADER = "X-Auth-Role";
    public static final String ROLE_ADMIN_VALUE = "admin";
    public static final String ROLE_USER_VALUE = "user";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtCookieAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            readAccessCookie(request)
                    .flatMap(jwtService::parseAccessToken)
                    .flatMap(this::loadActiveUser)
                    .ifPresent(entity -> {
                        populateSecurityContext(entity);
                        response.setHeader(ROLE_HEADER, entity.isAdmin() ? ROLE_ADMIN_VALUE : ROLE_USER_VALUE);
                    });
        }
        chain.doFilter(request, response);
    }

    private Optional<UserEntity> loadActiveUser(AccessTokenClaims claims) {
        return userRepository.findByIdAndDeletedFalse(claims.getUserId());
    }

    private Optional<String> readAccessCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> CookieService.ACCESS_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void populateSecurityContext(UserEntity entity) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                entity.isAdmin() ? AUTHORITY_ADMIN : AUTHORITY_USER
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                entity.getId(), null, List.of(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
