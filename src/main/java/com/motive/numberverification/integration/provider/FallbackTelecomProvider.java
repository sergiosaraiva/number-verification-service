package com.motive.numberverification.integration.provider;

import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simplified FallbackTelecomProvider for testing.
 * No WebClient or external dependencies required.
 */
@Component("fallbackTelecomProvider")
public class FallbackTelecomProvider implements TelecomProvider {

    private static final Logger logger = LoggerFactory.getLogger(FallbackTelecomProvider.class);
    
    @Override
    public VerificationStatus verifyPhoneNumber(String phoneNumber) {
        logger.info("Verifying phone number with fallback telecom provider");
        
        // Simulate API call with latency
        try {
            Thread.sleep(200); // Simulate 200ms latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Fallback provider verification completed");
        
        // For simplicity, always return MATCH from fallback provider
        return VerificationStatus.MATCH;
    }
    
    @Override
    public String getDevicePhoneNumber() {
        logger.info("Retrieving device phone number from fallback telecom provider");
        
        // Simulate API call with latency
        try {
            Thread.sleep(200); // Simulate 200ms latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Fallback provider device phone number retrieval completed");
        
        // Return a dummy phone number
        return "+0987654321";
    }
}