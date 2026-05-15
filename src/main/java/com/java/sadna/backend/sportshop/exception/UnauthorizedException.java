package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    private static final String CODE = "UNAUTHORIZED";
    private static final String DEFAULT_MESSAGE = "Authentication is required.";

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, CODE, DEFAULT_MESSAGE);
    }

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, CODE, message);
    }
}
