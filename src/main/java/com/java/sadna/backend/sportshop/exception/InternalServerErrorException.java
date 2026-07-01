package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApiException {

    private static final String CODE = "INTERNAL_SERVER_ERROR";
    private static final String DEFAULT_KEY = "http.internalError";

    public InternalServerErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, CODE, DEFAULT_KEY);
    }

    public InternalServerErrorException(String messageKey) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, CODE, messageKey);
    }

    public InternalServerErrorException(String messageKey, Object... args) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, CODE, messageKey, args);
    }
}
