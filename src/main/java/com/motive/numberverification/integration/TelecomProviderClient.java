package com.motive.numberverification.integration;

import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import com.motive.numberverification.integration.provider.TelecomProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TelecomProviderClient {

    private static final Logger logger = LoggerFactory.getLogger(TelecomProviderClient.class);
    
    private final TelecomProvider primaryProvider;
    private final TelecomProvider fallbackProvider;
    
    @Autowired
    public TelecomProviderClient(
            @Qualifier("defaultTelecomProvider") TelecomProvider primaryProvider,
            @Qualifier("fallbackTelecomProvider") TelecomProvider fallbackProvider) {
        this.primaryProvider = primaryProvider;
        this.fallbackProvider = fallbackProvider;
    }
    
    /**
     * Verify if the provided phone number matches the user's device.
     * Uses circuit breaker and retry patterns for resilience.
     */
    @CircuitBreaker(name = "telecomProvider", fallbackMethod = "fallbackVerify")
    @Retry(name = "telecomProvider")
    public VerificationStatus verifyPhoneNumber(String phoneNumber) {
        logger.info("Calling primary telecom provider to verify phone number");
        
        try {
            return primaryProvider.verifyPhoneNumber(phoneNumber);
        } catch (Exception e) {
            logger.error("Error calling primary telecom provider: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Fallback method for phone number verification when primary provider fails.
     */
    public VerificationStatus fallbackVerify(String phoneNumber, Exception e) {
        logger.warn("Primary provider failed, using fallback provider. Error: {}", e.getMessage());
        
        try {
            return fallbackProvider.verifyPhoneNumber(phoneNumber);
        } catch (Exception fallbackException) {
            logger.error("Fallback provider also failed: {}", fallbackException.getMessage());
            return VerificationStatus.INDETERMINATE;
        }
    }
    
    /**
     * Retrieve the phone number from the user's device.
     * Uses circuit breaker and retry patterns for resilience.
     */
    @CircuitBreaker(name = "telecomProvider", fallbackMethod = "fallbackGetDevicePhoneNumber")
    @Retry(name = "telecomProvider")
    public String getDevicePhoneNumber() {
        logger.info("Calling primary telecom provider to retrieve device phone number");
        
        try {
            return primaryProvider.getDevicePhoneNumber();
        } catch (Exception e) {
            logger.error("Error calling primary telecom provider: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Fallback method for retrieving device phone number when primary provider fails.
     */
    public String fallbackGetDevicePhoneNumber(Exception e) {
        logger.warn("Primary provider failed, using fallback provider. Error: {}", e.getMessage());
        
        try {
            return fallbackProvider.getDevicePhoneNumber();
        } catch (Exception fallbackException) {
            logger.error("Fallback provider also failed: {}", fallbackException.getMessage());
            return null;
        }
    }
}
