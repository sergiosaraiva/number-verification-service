package com.motive.numberverification.integration.provider;

import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Default implementation of the TelecomProvider interface.
 * This implementation communicates with the primary telecom API.
 */
@Component("defaultTelecomProvider")
public class DefaultTelecomProvider implements TelecomProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTelecomProvider.class);
    
    private final WebClient webClient;
    private final String apiUrl;
    private final String apiKey;
    
    public DefaultTelecomProvider(
            WebClient.Builder webClientBuilder,
            @Value("${telecom.provider.primary.url}") String apiUrl,
            @Value("${telecom.provider.primary.key}") String apiKey) {
        
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("X-API-Key", apiKey)
                .build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }
    
    @Override
    public VerificationStatus verifyPhoneNumber(String phoneNumber) {
        logger.info("Verifying phone number with primary telecom provider");
        
        // In a real implementation, this would make an API call to the telecom provider
        // For this example, we're just logging that we reached this point and returning a dummy result
        
        // Simulate API call with random result
        simulateApiLatency();
        
        logger.info("Primary provider verification completed");
        
        // For demo purposes, return a random status
        int random = ThreadLocalRandom.current().nextInt(10);
        if (random < 7) {
            return VerificationStatus.MATCH; // 70% chance
        } else if (random < 9) {
            return VerificationStatus.NO_MATCH; // 20% chance
        } else {
            return VerificationStatus.INDETERMINATE; // 10% chance
        }
    }
    
    @Override
    public String getDevicePhoneNumber() {
        logger.info("Retrieving device phone number from primary telecom provider");
        
        // In a real implementation, this would make an API call to the telecom provider
        // For this example, we're just logging that we reached this point and returning a dummy result
        
        // Simulate API call with latency
        simulateApiLatency();
        
        logger.info("Primary provider device phone number retrieval completed");
        
        // Return a dummy phone number
        return "+1234567890";
    }
    
    /**
     * Simulates API call latency for demonstration purposes.
     */
    private void simulateApiLatency() {
        try {
            // Simulate network latency between 100-300ms
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
