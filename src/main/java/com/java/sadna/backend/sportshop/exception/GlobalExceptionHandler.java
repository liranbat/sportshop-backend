package com.java.sadna.backend.sportshop.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // @PreAuthorize denials surface here (not in ExceptionTranslationFilter) because
    // they're thrown after the filter chain. Map "no principal" -> 401 so the frontend's
    // refresh-retry interceptor fires; keep 403 for "logged in but lacks the role".
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException e) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean anonymous = auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated();
        HttpStatus status = anonymous ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
        String code = anonymous ? "UNAUTHORIZED" : "FORBIDDEN";
        String message = anonymous ? "Authentication is required." : "You do not have permission to perform this action.";
        log.debug("Access denied [status={}]: {}", status.value(), e.getMessage());
        ApiError body = new ApiError(OffsetDateTime.now(), TRACE_ID_PLACEHOLDER, code, message);
        return ResponseEntity.status(status).body(body);
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
