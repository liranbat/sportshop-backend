package com.java.sadna.backend.sportshop.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // placeholder until real tracing (OpenTelemetry / Micrometer Tracing) is wired in
    private static final String TRACE_ID_PLACEHOLDER = "00000000-0000-0000-0000-000000000000";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException e) {
        log.warn("API exception [status={}, code={}]: {}", e.getStatus().value(), e.getCode(), e.getMessage());
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                TRACE_ID_PLACEHOLDER,
                e.getCode(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception e) {
        log.error("Unhandled exception", e);
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                TRACE_ID_PLACEHOLDER,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
