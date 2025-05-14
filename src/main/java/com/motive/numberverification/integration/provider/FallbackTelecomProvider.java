package com.motive.numberverification.integration.provider;

import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fallback implementation of the TelecomProvider interface.
 * This implementation is used when the primary provider fails.
 */
@Component("fallbackTelecomProvider")
public class FallbackTelecomProvider implements TelecomProvider {

    private static final Logger logger = LoggerFactory.getLogger(FallbackTelecomProvider.class);
    
    private final WebClient webClient;
    
    public FallbackTelecomProvider(
            WebClient.Builder webClientBuilder,
            @Value("${telecom.provider.fallback.url}") String apiUrl,
            @Value("${telecom.provider.fallback.key}") String apiKey) {
        
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("X-API-Key", apiKey)
                .build();
    }
    
    @Override
    public VerificationStatus verifyPhoneNumber(String phoneNumber) {
        logger.info("Verifying phone number with fallback telecom provider");
        
        // In a real implementation, this would make an API call to the fallback telecom provider
        // For this example, we're just logging that we reached this point and returning a dummy result
        
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
        
        // In a real implementation, this would make an API call to the fallback telecom provider
        // For this example, we're just logging that we reached this point and returning a dummy result
        
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
