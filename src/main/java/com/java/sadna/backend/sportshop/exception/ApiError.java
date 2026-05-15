package com.java.sadna.backend.sportshop.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class ApiError {

    private final OffsetDateTime timestamp;
    private final String traceId;
    private final String code;
    private final String message;

    public ApiError(OffsetDateTime timestamp, String traceId, String code, String message) {
        this.timestamp = timestamp;
        this.traceId = traceId;
        this.code = code;
        this.message = message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    @JsonProperty("trace_id")
    public String getTraceId() {
        return traceId;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
