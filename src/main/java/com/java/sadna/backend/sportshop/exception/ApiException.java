package com.java.sadna.backend.sportshop.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private static final Object[] NO_ARGS = new Object[0];

    private final HttpStatus status;
    private final String code;
    private final String messageKey;
    private final Object[] messageArgs;

    public ApiException(HttpStatus status, String code, String messageKey) {
        this(status, code, messageKey, NO_ARGS);
    }

    public ApiException(HttpStatus status, String code, String messageKey, Object... messageArgs) {
        super(messageKey);
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs != null ? messageArgs : NO_ARGS;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }
}
