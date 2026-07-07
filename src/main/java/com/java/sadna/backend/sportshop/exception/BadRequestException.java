package com.java.sadna.backend.sportshop.exception;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

    private static final String CODE = "BAD_REQUEST";
    private static final String DEFAULT_KEY = ErrorConstants.Http.BAD_REQUEST_DEFAULT;

    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST, CODE, DEFAULT_KEY);
    }

    public BadRequestException(String messageKey) {
        super(HttpStatus.BAD_REQUEST, CODE, messageKey);
    }

    public BadRequestException(String messageKey, Object... args) {
        super(HttpStatus.BAD_REQUEST, CODE, messageKey, args);
    }
}
