package com.java.sadna.backend.sportshop.config;

import java.time.Duration;

public class AuthProperties {

    private final String secret;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final boolean cookieSecure;

    public AuthProperties(String secret, Duration accessTokenTtl, Duration refreshTokenTtl, boolean cookieSecure) {
        this.secret = secret;
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
        this.cookieSecure = cookieSecure;
    }

    public String getSecret() {
        return secret;
    }

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }
}
