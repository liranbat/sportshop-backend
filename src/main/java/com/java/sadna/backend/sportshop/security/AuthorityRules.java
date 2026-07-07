package com.java.sadna.backend.sportshop.security;

public final class AuthorityRules {

    public static final String ADMIN = "hasRole('" + Role.Names.ADMIN + "')";
    public static final String AUTHENTICATED = "isAuthenticated()";

    private AuthorityRules() {}
}
