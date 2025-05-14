package com.motive.numberverification.common.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

/**
 * Global exception handler for the API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle custom API exceptions.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
        logger.error("API Exception: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .correlationId(generateCorrelationId())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle validation exceptions from request body validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.error("Validation error: {}", ex.getMessage());
        
        // Extract the first validation error message
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse("Validation error");
        
        ApiError error = ApiError.builder()
                .errorCode("VALIDATION_ERROR")
                .message(errorMessage)
                .path(request.getRequestURI())
                .correlationId(generateCorrelationId())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle constraint violation exceptions.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        logger.error("Constraint violation: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .errorCode("VALIDATION_ERROR")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .correlationId(generateCorrelationId())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle circuit breaker exceptions.
     */
    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiError> handleCallNotPermittedException(
            CallNotPermittedException ex, HttpServletRequest request) {
        
        logger.error("Circuit breaker open: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .errorCode("SERVICE_UNAVAILABLE")
                .message("Service temporarily unavailable. Please try again later.")
                .path(request.getRequestURI())
                .correlationId(generateCorrelationId())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * Handle resource access exceptions (connection timeouts, etc.).
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiError> handleResourceAccessException(
            ResourceAccessException ex, HttpServletRequest request) {
        
        logger.error("Resource access error: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .errorCode("EXTERNAL_SERVICE_ERROR")
                .message("Error accessing external service. Please try again later.")
                .path(request.getRequestURI())
                .correlationId(generateCorrelationId())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }
    
    /**
     * Handle all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception ex, HttpServletRequest request) {
        
        String correlationId = generateCorrelationId();
        logger.error("Unexpected error: {} [correlationId={}]", ex.getMessage(), correlationId, ex);
        
        ApiError error = ApiError.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .correlationId(correlationId)
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Generate a unique correlation ID for tracking errors.
     */
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
