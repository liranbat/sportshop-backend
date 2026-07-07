package com.java.sadna.backend.sportshop.exception;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {

    private static final String CODE = "CONFLICT";
    private static final String DEFAULT_KEY = ErrorConstants.Http.CONFLICT_DEFAULT;

    public ConflictException() {
        super(HttpStatus.CONFLICT, CODE, DEFAULT_KEY);
    }

    public ConflictException(String messageKey) {
        super(HttpStatus.CONFLICT, CODE, messageKey);
    }

    public ConflictException(String messageKey, Object... args) {
        super(HttpStatus.CONFLICT, CODE, messageKey, args);
    }
}
