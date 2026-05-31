package com.java.sadna.backend.sportshop.security;

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

// Reads the access_token cookie on every request, parses it via JwtService,
// and -- if valid AND the user is still active (not soft-deleted) -- populates
// SecurityContext with a UsernamePasswordAuthenticationToken whose principal is the
// Long userId and whose single authority reflects the isAdmin claim (ROLE_ADMIN vs
// ROLE_USER). Anything invalid / expired / missing / soft-deleted leaves the context
// untouched, which means SecurityConfig's authorize rules then decide whether the
// request is allowed (permitAll lanes) or 401'd.
//
// The soft-deleted check closes the ~15-min "valid JWT, user soft-deleted" window:
// without it, an admin-deleted (or self-deleted) user could keep authenticating with
// their cached access JWT until exp. One PK lookup on users(id) per authed request --
// the table is small and the row is hot in the buffer cache.
//
// NOT a @Component: instantiated by SecurityConfig.securityFilterChain so it
// only runs inside the Spring Security chain. If it were a bean, Spring Boot
// would also auto-register it as a generic servlet filter and we'd double-run.
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORITY_ADMIN = "ROLE_ADMIN";
    public static final String AUTHORITY_USER = "ROLE_USER";

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
                    .filter(this::userStillActive)
                    .ifPresent(this::populateSecurityContext);
        }
        chain.doFilter(request, response);
    }

    private boolean userStillActive(AccessTokenClaims claims) {
        return userRepository.findByIdAndDeletedFalse(claims.getUserId()).isPresent();
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

    private void populateSecurityContext(AccessTokenClaims claims) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                claims.isAdmin() ? AUTHORITY_ADMIN : AUTHORITY_USER
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                claims.getUserId(), null, List.of(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
