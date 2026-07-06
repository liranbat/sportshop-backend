package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.AppProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieService {

    public static final String ACCESS_COOKIE_NAME = "sportshop_access_token";
    public static final String REFRESH_COOKIE_NAME = "sportshop_refresh_token";

    private static final String ACCESS_COOKIE_PATH = "/";
    private static final String SAME_SITE_LAX = "Lax";

    private final String refreshCookiePath;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final boolean cookieSecure;

    public CookieService(AppProperties appProperties) {
        this.refreshCookiePath = appProperties.getApi().getPathPrefix() + "/auth/refresh";
        this.accessTokenTtl = appProperties.getAuth().getAccessTokenTtl();
        this.refreshTokenTtl = appProperties.getAuth().getRefreshTokenTtl();
        this.cookieSecure = appProperties.getAuth().isCookieSecure();
    }

    public ResponseCookie buildAccessCookie(String jwt) {
        return baseBuilder(ACCESS_COOKIE_NAME, jwt, ACCESS_COOKIE_PATH)
                .maxAge(accessTokenTtl)
                .build();
    }

    public ResponseCookie buildRefreshCookie(String token) {
        return baseBuilder(REFRESH_COOKIE_NAME, token, refreshCookiePath)
                .maxAge(refreshTokenTtl)
                .build();
    }

    // Clear cookies must mirror the original Path attribute or the browser
    // creates a second cookie at the new Path instead of deleting the first.
    public ResponseCookie clearAccessCookie() {
        return baseBuilder(ACCESS_COOKIE_NAME, "", ACCESS_COOKIE_PATH)
                .maxAge(0)
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return baseBuilder(REFRESH_COOKIE_NAME, "", refreshCookiePath)
                .maxAge(0)
                .build();
    }

    public Optional<String> readRefreshCookie(HttpServletRequest request) {
        return readCookieValue(request, REFRESH_COOKIE_NAME);
    }

    // Generic cookie-value reader. Consumed by the JWT auth filter for the access cookie
    // and by readRefreshCookie above; kept public so future filters/interceptors can reuse
    // the null-safe traversal instead of re-implementing it.
    public Optional<String> readCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    // Emits both Set-Cookie headers that wipe the access + refresh auth cookies. Both
    // cookies must be cleared together to fully log a user out, so a single helper keeps
    // AuthService.logout and UserService.deleteAccount from drifting apart.
    public void clearAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString());
    }

    private ResponseCookie.ResponseCookieBuilder baseBuilder(String name, String value, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(SAME_SITE_LAX)
                .path(path);
    }
}
