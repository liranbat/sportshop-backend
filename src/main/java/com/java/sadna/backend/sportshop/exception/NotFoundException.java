package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    private static final String CODE = "NOT_FOUND";
    private static final String DEFAULT_KEY = "http.notFound.default";

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND, CODE, DEFAULT_KEY);
    }

    public NotFoundException(String messageKey) {
        super(HttpStatus.NOT_FOUND, CODE, messageKey);
    }

    public NotFoundException(String messageKey, Object... args) {
        super(HttpStatus.NOT_FOUND, CODE, messageKey, args);
    }
}
