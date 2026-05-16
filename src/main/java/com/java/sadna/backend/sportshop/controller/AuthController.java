package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.auth.api.AuthApi;
import com.java.sadna.backend.sportshop.api.generated.auth.model.LoginRequestDto;
import com.java.sadna.backend.sportshop.api.generated.auth.model.RegisterRequestDto;
import com.java.sadna.backend.sportshop.api.generated.auth.model.UserResponseDto;
import com.java.sadna.backend.sportshop.exception.UnauthorizedException;
import com.java.sadna.backend.sportshop.mapper.UserToUserResponseDtoMapper;
import com.java.sadna.backend.sportshop.service.AuthService;
import com.java.sadna.backend.sportshop.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserToUserResponseDtoMapper userToUserResponseDtoMapper;
    
    // Spring injects request-scoped proxies, so even though this controller is a singleton,
    // each call reads/writes the cookies of its own HTTP request.
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    public AuthController(AuthService authService,
                          UserToUserResponseDtoMapper userToUserResponseDtoMapper,
                          HttpServletRequest httpServletRequest,
                          HttpServletResponse httpServletResponse) {
        this.authService = authService;
        this.userToUserResponseDtoMapper = userToUserResponseDtoMapper;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponseDto> register(RegisterRequestDto registerRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userToUserResponseDtoMapper.map(authService.register(registerRequestDto)));
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponseDto> login(LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(
                userToUserResponseDtoMapper.map(authService.login(loginRequestDto, httpServletResponse))
        );
    }

    // Deliberately permitAll on a state-changing endpoint: an unauthenticated caller
    // still gets 204 + cleared cookies so the frontend can treat logout as always-succeeds.
    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> logout() {
        authService.logout(currentUserId().orElse(null), httpServletResponse);
        return ResponseEntity.noContent().build();
    }

    // permitAll because refresh runs without a valid access token by definition;
    // the refresh_token cookie itself is verified inside AuthService.
    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponseDto> refresh() {
        String refreshToken = readRefreshCookie()
                .orElseThrow(() -> new UnauthorizedException("Refresh token is missing."));
        return ResponseEntity.ok(
                userToUserResponseDtoMapper.map(authService.refresh(refreshToken, httpServletResponse))
        );
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMe() {
        // @PreAuthorize already guaranteed an authenticated principal; the orElseThrow
        // only fires if something other than JwtCookieAuthenticationFilter populated the
        // SecurityContext with a non-Long principal.
        Long userId = currentUserId().orElseThrow(UnauthorizedException::new);
        return ResponseEntity.ok(userToUserResponseDtoMapper.map(authService.getMe(userId)));
    }

    private Optional<Long> currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Optional.empty();
        }
        // Anonymous auth (set by Spring's AnonymousAuthenticationFilter on
        // permitAll routes) has a String "anonymousUser" principal; only our
        // JwtCookieAuthenticationFilter puts a Long there.
        return auth.getPrincipal() instanceof Long userId ? Optional.of(userId) : Optional.empty();
    }

    private Optional<String> readRefreshCookie() {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> CookieService.REFRESH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
