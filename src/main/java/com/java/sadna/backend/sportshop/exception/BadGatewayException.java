package com.java.sadna.backend.sportshop.exception;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import org.springframework.http.HttpStatus;

public class BadGatewayException extends ApiException {

    private static final String CODE = "BAD_GATEWAY";
    private static final String DEFAULT_KEY = ErrorConstants.Http.BAD_GATEWAY_DEFAULT;

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
