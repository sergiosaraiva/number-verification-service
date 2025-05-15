package com.motive.numberverification.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * OAuth 2.0 Authorization filter for validating access tokens.
 * This simplified version accepts specific tokens for testing purposes.
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String MOCK_TOKEN = "mock_sandbox_access_token";
    private static final String REQUIRED_SCOPE = "dpv:FraudPreventionAndDetection#number-verification-verify-read";
    
    private final MeterRegistry meterRegistry;
    
    public AuthenticationFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        logger.info("===> AuthenticationFilter processing request: {}", request.getRequestURI());
        
        // Extract the token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header");
            meterRegistry.counter("authentication_failure", "reason", "missing_token").increment();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        
        String token = authorizationHeader.substring(7);
        
        try {
            // For testing purposes, accept the mock token
            if (MOCK_TOKEN.equals(token)) {
                // Create authentication with required scope
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        "test-user",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("SCOPE_" + REQUIRED_SCOPE))
                );
                
                // Set authentication in the Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.info("Successfully authenticated test user with mock token");
                meterRegistry.counter("authentication_success").increment();
            } else {
                // In a real implementation, validate the token and extract scopes
                logger.warn("Invalid token: Not the mock token");
                meterRegistry.counter("authentication_failure", "reason", "invalid_token").increment();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
            
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            meterRegistry.counter("authentication_failure", "reason", "authentication_error").increment();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed");
            return;
        }
        
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}