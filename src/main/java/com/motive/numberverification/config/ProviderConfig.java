package com.motive.numberverification.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Configuration for telecom provider integration.
 */
@Configuration
public class ProviderConfig {

    /**
     * WebClient bean for making HTTP requests to external providers.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)); // 16MB buffer
    }
    
    /**
     * CircuitBreakerRegistry for managing circuit breakers.
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)                // Open circuit when 50% of requests fail
                .waitDurationInOpenState(Duration.ofSeconds(30))  // Wait 30 seconds before attempting recovery
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)                   // Consider last 10 requests for failure rate
                .minimumNumberOfCalls(5)                 // Minimum calls before calculating error rate
                .permittedNumberOfCallsInHalfOpenState(3) // Allow 3 test calls when half-open
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
        
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }
    
    /**
     * RetryRegistry for managing retry policies.
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)                          // Maximum number of retry attempts
                .waitDuration(Duration.ofMillis(500))    // Initial wait duration
                .retryExceptions(                        // Exceptions to retry on
                        java.io.IOException.class,
                        java.net.SocketTimeoutException.class,
                        org.springframework.web.client.ResourceAccessException.class
                )
                .build();
        
        return RetryRegistry.of(retryConfig);
    }
}
