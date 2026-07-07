package com.java.sadna.backend.sportshop.security;

public enum Role {

    USER(Names.USER_AUTHORITY, "user"),
    ADMIN(Names.ADMIN_AUTHORITY, "admin");

    public static final class Names {
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
        public static final String USER_AUTHORITY = "ROLE_" + USER;
        public static final String ADMIN_AUTHORITY = "ROLE_" + ADMIN;

        private Names() {}
    }

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
