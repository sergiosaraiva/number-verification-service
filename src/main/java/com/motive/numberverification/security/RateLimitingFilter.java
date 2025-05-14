package com.motive.numberverification.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter for rate limiting API requests.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int requestsPerMinute;
    private final MeterRegistry meterRegistry;
    
    public RateLimitingFilter(
            @Value("${security.rate-limiting.requests-per-minute:60}") int requestsPerMinute,
            MeterRegistry meterRegistry) {
        this.requestsPerMinute = requestsPerMinute;
        this.meterRegistry = meterRegistry;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        logger.debug("Rate limiting filter processing request: {}", request.getRequestURI());
        
        // Get client IP address or API key for rate limiting
        String clientKey = getClientKey(request);
        
        // Get or create rate limiting bucket for this client
        Bucket bucket = buckets.computeIfAbsent(clientKey, this::createNewBucket);
        
        // Try to consume a token from the bucket
        if (bucket.tryConsume(1)) {
            // Request is allowed, continue the filter chain
            logger.debug("Request allowed for client: {}", clientKey);
            filterChain.doFilter(request, response);
        } else {
            // Request is rate limited
            logger.warn("Rate limit exceeded for client: {}", clientKey);
            meterRegistry.counter("rate_limit_exceeded").increment();
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Try again later.\"}");
        }
    }
    
    /**
     * Extract client identifier for rate limiting.
     * Uses API key from header if available, otherwise falls back to IP address.
     */
    private String getClientKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return "api-key:" + apiKey;
        }
        
        return "ip:" + request.getRemoteAddr();
    }
    
    /**
     * Create a new rate limiting bucket for a client.
     */
    private Bucket createNewBucket(String clientKey) {
        // Create bandwidth limit: requestsPerMinute tokens per minute
        Bandwidth limit = Bandwidth.classic(requestsPerMinute, 
                Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
