package com.java.sadna.backend.sportshop.exception;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MDC_TRACE_ID = "traceId";
    private static final String TRACE_ID_UNKNOWN = "unknown";
    private static final Object[] NO_ARGS = new Object[0];
    private static final Locale MESSAGES_LOCALE = Locale.ROOT;

    private final MessageSource messages;

    public GlobalExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    private static String currentTraceId() {
        String id = MDC.get(MDC_TRACE_ID);
        return (id != null && !id.isEmpty()) ? id : TRACE_ID_UNKNOWN;
    }

    private String resolve(String key, Object... args) {
        return messages.getMessage(key, args != null ? args : NO_ARGS, MESSAGES_LOCALE);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException e) {
        String resolved = resolve(e.getMessageKey(), e.getMessageArgs());
        log.warn("API exception [status={}, code={}, key={}]",
                e.getStatus().value(), e.getCode(), e.getMessageKey());
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                currentTraceId(),
                e.getCode(),
                resolved
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
        String message = resolve(anonymous ? ErrorConstants.Http.UNAUTHORIZED : ErrorConstants.Http.FORBIDDEN);
        log.debug("Access denied [status={}]: {}", status.value(), e.getMessage());
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), code, message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.debug("Bad request param [name={}, value={}]: {}", e.getName(), e.getValue(), e.getMessage());
        String message = resolve(ErrorConstants.Http.BAD_REQUEST_TYPE_MISMATCH, e.getValue(), e.getName());
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "BAD_REQUEST", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (message.isEmpty()) {
            message = resolve(ErrorConstants.Http.BAD_REQUEST_VALIDATION_FAILED);
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
            message = resolve(ErrorConstants.Http.BAD_REQUEST_CONSTRAINT_FAILED);
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
        String message = resolve(ErrorConstants.Http.BAD_REQUEST_MISSING_PARAM, e.getParameterName());
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
                resolve(ErrorConstants.Http.BAD_REQUEST_MALFORMED_BODY)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.debug("Method not supported [method={}]: {}", e.getMethod(), e.getMessage());
        String message = resolve(ErrorConstants.Http.METHOD_NOT_ALLOWED, e.getMethod());
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "METHOD_NOT_ALLOWED", message);
        HttpHeaders headers = new HttpHeaders();
        if (e.getSupportedHttpMethods() != null) {
            headers.setAllow(e.getSupportedHttpMethods());
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.debug("Multipart upload exceeded the configured size cap: {}", e.getMessage());
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                currentTraceId(),
                "IMAGE_FILE_TOO_LARGE",
                resolve(ErrorConstants.Http.PAYLOAD_TOO_LARGE_UPLOAD)
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.debug("Unsupported media type [contentType={}]: {}", e.getContentType(), e.getMessage());
        String message = e.getContentType() != null
                ? resolve(ErrorConstants.Http.MEDIA_TYPE_UNSUPPORTED, e.getContentType())
                : resolve(ErrorConstants.Http.MEDIA_TYPE_MISSING);
        ApiError body = new ApiError(OffsetDateTime.now(), currentTraceId(), "UNSUPPORTED_MEDIA_TYPE", message);
        HttpHeaders headers = new HttpHeaders();
        if (!e.getSupportedMediaTypes().isEmpty()) {
            headers.setAccept(e.getSupportedMediaTypes());
        }
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).headers(headers).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException e) {
        log.debug("No matching handler [path={}]: {}", e.getResourcePath(), e.getMessage());
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                currentTraceId(),
                "NOT_FOUND",
                resolve(ErrorConstants.Http.NOT_FOUND_DEFAULT)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception e) {
        log.error("Unhandled exception", e);
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                currentTraceId(),
                "INTERNAL_SERVER_ERROR",
                resolve(ErrorConstants.Http.INTERNAL_ERROR)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
