package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class PayloadTooLargeException extends ApiException {

    private static final String CODE = "PAYLOAD_TOO_LARGE";
    private static final String DEFAULT_KEY = "http.payloadTooLarge.default";

    public PayloadTooLargeException() {
        super(HttpStatus.PAYLOAD_TOO_LARGE, CODE, DEFAULT_KEY);
    }

    public PayloadTooLargeException(String messageKey) {
        super(HttpStatus.PAYLOAD_TOO_LARGE, CODE, messageKey);
    }

    public PayloadTooLargeException(String messageKey, Object... args) {
        super(HttpStatus.PAYLOAD_TOO_LARGE, CODE, messageKey, args);
    }
}
