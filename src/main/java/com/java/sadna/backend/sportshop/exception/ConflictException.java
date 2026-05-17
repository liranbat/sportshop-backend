package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {

    private static final String CODE = "CONFLICT";
    private static final String DEFAULT_MESSAGE = "The request conflicts with the current state.";

    public ConflictException() {
        super(HttpStatus.CONFLICT, CODE, DEFAULT_MESSAGE);
    }

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, CODE, message);
    }
}
