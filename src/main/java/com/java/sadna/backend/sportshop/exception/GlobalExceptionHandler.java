package com.java.sadna.backend.sportshop.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String MDC_TRACE_ID = "traceId";
    private static final String TRACE_ID_UNKNOWN = "unknown";

    private static String currentTraceId() {
        String id = MDC.get(MDC_TRACE_ID);
        return (id != null && !id.isEmpty()) ? id : TRACE_ID_UNKNOWN;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException e) {
        log.warn("API exception [status={}, code={}]: {}", e.getStatus().value(), e.getCode(), e.getMessage());
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                currentTraceId(),
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
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), code, message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.debug("Bad request param [name={}, value={}]: {}", e.getName(), e.getValue(), e.getMessage());
        String message = "Invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'.";
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "BAD_REQUEST", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (message.isEmpty()) {
            message = "Request body validation failed.";
        }
        log.debug("Validation failed: {}", message);
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "BAD_REQUEST", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String formatFieldError(FieldError fe) {
        String defaultMessage = fe.getDefaultMessage();
        return fe.getField() + ": " + (defaultMessage != null ? defaultMessage : "invalid");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.joining("; "));
        if (message.isEmpty()) {
            message = "Request parameters failed validation.";
        }
        log.debug("Constraint violation: {}", message);
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "BAD_REQUEST", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String formatConstraintViolation(ConstraintViolation<?> v) {
        // propertyPath looks like "revokeAdminSession.sessionId"; keep just the leaf param name.
        String path = v.getPropertyPath().toString();
        int dot = path.lastIndexOf('.');
        String name = dot >= 0 ? path.substring(dot + 1) : path;
        String defaultMessage = v.getMessage();
        return name + ": " + (defaultMessage != null ? defaultMessage : "invalid");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException e) {
        log.debug("Missing required parameter [name={}]: {}", e.getParameterName(), e.getMessage());
        String message = "Required parameter '" + e.getParameterName() + "' is missing.";
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "BAD_REQUEST", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException e) {
        log.debug("Malformed request body: {}", e.getMessage());
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                currentTraceId(),
                "BAD_REQUEST",
                "Malformed request body."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.debug("Method not supported [method={}]: {}", e.getMethod(), e.getMessage());
        String message = "Method '" + e.getMethod() + "' is not supported for this endpoint.";
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "METHOD_NOT_ALLOWED", message);
        HttpHeaders headers = new HttpHeaders();
        if (e.getSupportedHttpMethods() != null) {
            headers.setAllow(e.getSupportedHttpMethods());
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception e) {
        log.error("Unhandled exception", e);
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                currentTraceId(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
