package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

    private static final String CODE = "FORBIDDEN";
    private static final String DEFAULT_MESSAGE = "You are not allowed to perform this action.";

    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, CODE, DEFAULT_MESSAGE);
    }

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, CODE, message);
    }
}
