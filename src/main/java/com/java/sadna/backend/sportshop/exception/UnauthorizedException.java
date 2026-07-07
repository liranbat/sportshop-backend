package com.java.sadna.backend.sportshop.exception;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    private static final String CODE = "UNAUTHORIZED";
    private static final String DEFAULT_KEY = ErrorConstants.Http.UNAUTHORIZED;

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, CODE, DEFAULT_KEY);
    }

    public UnauthorizedException(String messageKey) {
        super(HttpStatus.UNAUTHORIZED, CODE, messageKey);
    }

    public UnauthorizedException(String messageKey, Object... args) {
        super(HttpStatus.UNAUTHORIZED, CODE, messageKey, args);
    }
}
