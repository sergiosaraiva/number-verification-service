package com.motive.numberverification.common.exception;

/**
 * Custom exception for API-related errors.
 */
public class ApiException extends RuntimeException {

    private final String errorCode;
    
    public ApiException(String message) {
        super(message);
        this.errorCode = "GENERIC_ERROR";
    }
    
    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERIC_ERROR";
    }
    
    public ApiException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
