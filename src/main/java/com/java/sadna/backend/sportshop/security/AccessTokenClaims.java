package com.java.sadna.backend.sportshop.security;

public class AccessTokenClaims {

    private final long userId;

    public AccessTokenClaims(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
