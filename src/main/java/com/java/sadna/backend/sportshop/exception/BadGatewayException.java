package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class BadGatewayException extends ApiException {

    private static final String CODE = "BAD_GATEWAY";
    private static final String DEFAULT_KEY = "http.badGateway.default";

    public BadGatewayException() {
        super(HttpStatus.BAD_GATEWAY, CODE, DEFAULT_KEY);
    }

    public BadGatewayException(String messageKey) {
        super(HttpStatus.BAD_GATEWAY, CODE, messageKey);
    }

    public BadGatewayException(String messageKey, Object... args) {
        super(HttpStatus.BAD_GATEWAY, CODE, messageKey, args);
    }
}
