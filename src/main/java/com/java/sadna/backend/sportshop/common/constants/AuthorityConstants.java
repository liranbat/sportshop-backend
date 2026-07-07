package com.java.sadna.backend.sportshop.common.constants;

import com.java.sadna.backend.sportshop.security.Role;

public final class AuthorityConstants {

    public static final String ADMIN = "hasRole('" + Role.Names.ADMIN + "')";
    public static final String AUTHENTICATED = "isAuthenticated()";

    private AuthorityConstants() {}
}
