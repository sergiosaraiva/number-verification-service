package com.motive.numberverification.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter for JWT token-based authentication.
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    private final String jwtSecret;
    private final MeterRegistry meterRegistry;
    
    public AuthenticationFilter(
            @Value("${security.jwt.secret}") String jwtSecret,
            MeterRegistry meterRegistry) {
        this.jwtSecret = jwtSecret;
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
        
        String token = authorizationHeader.substring(7);
        
        try {
            // Validate the token
            Claims claims = parseToken(token);
            
            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                logger.warn("Expired JWT token");
                meterRegistry.counter("authentication_failure", "reason", "expired_token").increment();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Expired JWT token");
                return;
            }
            
            // Extract user information from the token
            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            
            // Create authentication object
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    roles != null ? roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()) : Collections.emptyList()
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
    
    /**
     * Parse and validate JWT token.
     */
    private Claims parseToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
