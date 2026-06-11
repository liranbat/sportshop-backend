package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApiException {

    private static final String CODE = "INTERNAL_SERVER_ERROR";
    private static final String DEFAULT_MESSAGE = "An unexpected error occurred.";

    public InternalServerErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, CODE, DEFAULT_MESSAGE);
    }

    public InternalServerErrorException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, CODE, message);
    }
}
