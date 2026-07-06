package com.java.sadna.backend.sportshop.security;

public enum Role {

    USER("ROLE_USER", "user"),
    ADMIN("ROLE_ADMIN", "admin");

    private final String authority;
    private final String headerValue;

    Role(String authority, String headerValue) {
        this.authority = authority;
        this.headerValue = headerValue;
    }

    public String authority() {
        return authority;
    }

    public String headerValue() {
        return headerValue;
    }
}
