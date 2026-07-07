package com.java.sadna.backend.sportshop.security;

import com.java.sadna.backend.sportshop.common.constants.ApiHeaderConstants;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.repository.UserRepository;
import com.java.sadna.backend.sportshop.service.CookieService;
import com.java.sadna.backend.sportshop.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CookieService cookieService;

    public JwtCookieAuthenticationFilter(JwtService jwtService,
                                         UserRepository userRepository,
                                         CookieService cookieService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.cookieService = cookieService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            cookieService.readCookieValue(request, cookieService.accessCookieName())
                    .flatMap(jwtService::parseAccessToken)
                    .flatMap(this::loadActiveUser)
                    .ifPresent(entity -> {
                        populateSecurityContext(entity);
                        response.setHeader(ApiHeaderConstants.X_AUTH_ROLE, (entity.isAdmin() ? Role.ADMIN : Role.USER).headerValue());
                    });
        }
        chain.doFilter(request, response);
    }

    private Optional<UserEntity> loadActiveUser(AccessTokenClaims claims) {
        return userRepository.findByIdAndDeletedFalse(claims.getUserId());
    }

    private void populateSecurityContext(UserEntity entity) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                (entity.isAdmin() ? Role.ADMIN : Role.USER).authority()
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                entity.getId(), null, List.of(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
