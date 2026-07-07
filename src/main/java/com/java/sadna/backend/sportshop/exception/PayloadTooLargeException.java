package com.java.sadna.backend.sportshop.exception;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import org.springframework.http.HttpStatus;

public class PayloadTooLargeException extends ApiException {

    private static final String CODE = "PAYLOAD_TOO_LARGE";
    private static final String DEFAULT_KEY = ErrorConstants.Http.PAYLOAD_TOO_LARGE_DEFAULT;

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
