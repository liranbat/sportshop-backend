package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.ApiProperties;
import com.java.sadna.backend.sportshop.config.AuthProperties;
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

    private static final String ACCESS_COOKIE_PATH = "/";
    private static final String SAME_SITE_LAX = "Lax";

    private final String accessCookieName;
    private final String refreshCookieName;
    private final String refreshCookiePath;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final boolean cookieSecure;

    public CookieService(AuthProperties authProperties, ApiProperties apiProperties) {
        this.accessCookieName = authProperties.getCookie().getAccessName();
        this.refreshCookieName = authProperties.getCookie().getRefreshName();
        this.refreshCookiePath = apiProperties.getPathPrefix() + "/auth/refresh";
        this.accessTokenTtl = authProperties.getAccessTokenTtl();
        this.refreshTokenTtl = authProperties.getRefreshTokenTtl();
        this.cookieSecure = authProperties.isCookieSecure();
    }

    public String accessCookieName() {
        return accessCookieName;
    }

    public String refreshCookieName() {
        return refreshCookieName;
    }

    public ResponseCookie buildAccessCookie(String jwt) {
        return baseBuilder(accessCookieName, jwt, ACCESS_COOKIE_PATH)
                .maxAge(accessTokenTtl)
                .build();
    }

    public ResponseCookie buildRefreshCookie(String token) {
        return baseBuilder(refreshCookieName, token, refreshCookiePath)
                .maxAge(refreshTokenTtl)
                .build();
    }

    // Clear cookies must mirror the original Path attribute or the browser
    // creates a second cookie at the new Path instead of deleting the first.
    public ResponseCookie clearAccessCookie() {
        return baseBuilder(accessCookieName, "", ACCESS_COOKIE_PATH)
                .maxAge(0)
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return baseBuilder(refreshCookieName, "", refreshCookiePath)
                .maxAge(0)
                .build();
    }

    public Optional<String> readRefreshCookie(HttpServletRequest request) {
        return readCookieValue(request, refreshCookieName);
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
