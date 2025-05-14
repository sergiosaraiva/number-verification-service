package com.motive.numberverification.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter for JWT token-based authentication.
 * This simplified version accepts any Bearer token for testing purposes.
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    private final MeterRegistry meterRegistry;
    
    public AuthenticationFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        logger.debug("Authentication filter processing request: {}", request.getRequestURI());
        
        // Extract the token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header");
            meterRegistry.counter("authentication_failure", "reason", "missing_token").increment();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        
        try {
            // For testing purposes, accept any token
            String username = "test-user";
            
            // Create authentication object with ADMIN role
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            
            // Set authentication in the Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            logger.debug("Successfully authenticated user: {}", username);
            meterRegistry.counter("authentication_success").increment();
            
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            meterRegistry.counter("authentication_failure", "reason", "invalid_token").increment();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            return;
        }
        
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}