package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

    private static final String CODE = "FORBIDDEN";
    private static final String DEFAULT_KEY = "http.forbidden";

    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, CODE, DEFAULT_KEY);
    }

    public ForbiddenException(String messageKey) {
        super(HttpStatus.FORBIDDEN, CODE, messageKey);
    }

    public ForbiddenException(String messageKey, Object... args) {
        super(HttpStatus.FORBIDDEN, CODE, messageKey, args);
    }
}
