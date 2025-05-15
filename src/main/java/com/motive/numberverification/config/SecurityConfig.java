package com.motive.numberverification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.motive.numberverification.security.AuthenticationFilter;
import com.motive.numberverification.security.RateLimitingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final AuthenticationFilter authenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;
    
    public SecurityConfig(AuthenticationFilter authenticationFilter, 
                         RateLimitingFilter rateLimitingFilter) {
        this.authenticationFilter = authenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}