package com.java.sadna.backend.sportshop.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.auth")
@Validated
public class AuthProperties {

    private final String secret;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final boolean cookieSecure;
    private final int refreshTokenBytes;
    private final int minSecretBytes;
    private final Cookie cookie;

    public AuthProperties(
            @NotBlank String secret,
            @NotNull Duration accessTokenTtl,
            @NotNull Duration refreshTokenTtl,
            boolean cookieSecure,
            @DefaultValue("32") @Min(1) int refreshTokenBytes,
            @DefaultValue("32") @Min(1) int minSecretBytes,
            @DefaultValue @Valid Cookie cookie) {
        this.secret = secret;
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
        this.cookieSecure = cookieSecure;
        this.refreshTokenBytes = refreshTokenBytes;
        this.minSecretBytes = minSecretBytes;
        this.cookie = cookie;
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

    public int getRefreshTokenBytes() {
        return refreshTokenBytes;
    }

    public int getMinSecretBytes() {
        return minSecretBytes;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public static class Cookie {

        private final String accessName;
        private final String refreshName;

        public Cookie(
                @DefaultValue("sportshop_access_token") @NotBlank String accessName,
                @DefaultValue("sportshop_refresh_token") @NotBlank String refreshName) {
            this.accessName = accessName;
            this.refreshName = refreshName;
        }

        public String getAccessName() {
            return accessName;
        }

        public String getRefreshName() {
            return refreshName;
        }
    }
}
