package com.java.sadna.backend.sportshop.security;

public class AccessTokenClaims {

    private final long userId;
    private final boolean admin;

    public AccessTokenClaims(long userId, boolean admin) {
        this.userId = userId;
        this.admin = admin;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return admin;
    }
}
