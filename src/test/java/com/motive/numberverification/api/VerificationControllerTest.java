package com.motive.numberverification.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motive.numberverification.api.model.DevicePhoneNumberResponse;
import com.motive.numberverification.api.model.VerificationRequest;
import com.motive.numberverification.api.model.VerificationResponse;
import com.motive.numberverification.service.VerificationService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VerificationControllerTest {

    @Configuration
    @EnableWebSecurity
    @Profile("test")
    static class TestSecurityConfig {
        @Bean
        @Primary
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VerificationService verificationService;

    @BeforeEach
    public void setup() {
        when(verificationService.verifyPhoneNumber(any(VerificationRequest.class))).thenReturn(true);
        when(verificationService.getDevicePhoneNumber()).thenReturn("+1234567890");
    }

    @Test
    public void verifyPhoneNumber_shouldReturnMatchResult() throws Exception {
        // Given
        VerificationRequest request = new VerificationRequest();
        request.setPhoneNumber("+1234567890");

        // When
        MvcResult result = mockMvc.perform(post("/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        VerificationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), VerificationResponse.class);
        
        assertThat(response).isNotNull();
        assertThat(response.isDevicePhoneNumberVerified()).isTrue();
    }

    @Test
    public void getDevicePhoneNumber_shouldReturnPhoneNumber() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/device-phone-number"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        DevicePhoneNumberResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), DevicePhoneNumberResponse.class);
        
        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isEqualTo("+1234567890");
    }

    @Test
    public void verifyPhoneNumber_withInvalidPhoneNumber_shouldReturnBadRequest() throws Exception {
        // Given
        VerificationRequest request = new VerificationRequest();
        request.setPhoneNumber("invalid-phone-number");

        // When/Then
        mockMvc.perform(post("/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}