package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

    private static final String CODE = "BAD_REQUEST";
    private static final String DEFAULT_KEY = "http.badRequest.default";

    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST, CODE, DEFAULT_KEY);
    }

    public BadRequestException(String messageKey) {
        super(HttpStatus.BAD_REQUEST, CODE, messageKey);
    }

    public BadRequestException(String messageKey, Object... args) {
        super(HttpStatus.BAD_REQUEST, CODE, messageKey, args);
    }
}
