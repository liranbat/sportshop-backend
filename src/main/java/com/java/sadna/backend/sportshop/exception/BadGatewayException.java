package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class BadGatewayException extends ApiException {

    private static final String CODE = "BAD_GATEWAY";
    private static final String DEFAULT_MESSAGE = "An upstream service is currently unavailable.";

    public BadGatewayException() {
        super(HttpStatus.BAD_GATEWAY, CODE, DEFAULT_MESSAGE);
    }

    public BadGatewayException(String message) {
        super(HttpStatus.BAD_GATEWAY, CODE, message);
    }
}
