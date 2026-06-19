package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class PayloadTooLargeException extends ApiException {

    private static final String CODE = "PAYLOAD_TOO_LARGE";
    private static final String DEFAULT_MESSAGE = "The request payload is too large.";

    public PayloadTooLargeException() {
        super(HttpStatus.PAYLOAD_TOO_LARGE, CODE, DEFAULT_MESSAGE);
    }

    public PayloadTooLargeException(String message) {
        super(HttpStatus.PAYLOAD_TOO_LARGE, CODE, message);
    }
}
