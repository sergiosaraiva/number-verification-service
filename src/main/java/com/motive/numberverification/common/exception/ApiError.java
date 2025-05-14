package com.motive.numberverification.common.exception;

import java.time.Instant;

/**
 * Standard error response format for the API.
 */
public class ApiError {

    private final String errorCode;
    private final String message;
    private final Instant timestamp;
    private final String path;
    private final String correlationId;
    
    private ApiError(Builder builder) {
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.timestamp = Instant.now();
        this.path = builder.path;
        this.correlationId = builder.correlationId;
    }
    
    // Builder pattern for the ApiError
    public static class Builder {
        private String errorCode;
        private String message;
        private String path;
        private String correlationId;
        
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder path(String path) {
            this.path = path;
            return this;
        }
        
        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }
        
        public ApiError build() {
            return new ApiError(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
