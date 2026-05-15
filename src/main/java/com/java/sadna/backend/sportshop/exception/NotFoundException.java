package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    private static final String CODE = "NOT_FOUND";
    private static final String DEFAULT_MESSAGE = "Resource not found.";

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND, CODE, DEFAULT_MESSAGE);
    }

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, CODE, message);
    }
}
