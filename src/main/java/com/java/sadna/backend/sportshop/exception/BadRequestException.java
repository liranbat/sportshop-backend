package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

    private static final String CODE = "BAD_REQUEST";
    private static final String DEFAULT_MESSAGE = "The request is invalid.";

    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST, CODE, DEFAULT_MESSAGE);
    }

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, CODE, message);
    }
}
