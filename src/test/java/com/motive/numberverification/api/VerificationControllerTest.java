package com.motive.numberverification.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motive.numberverification.api.model.DevicePhoneNumberResponse;
import com.motive.numberverification.api.model.VerificationRequest;
import com.motive.numberverification.api.model.VerificationResponse;
import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import com.motive.numberverification.integration.TelecomProviderClient;

@SpringBootTest
@AutoConfigureMockMvc
public class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TelecomProviderClient telecomProviderClient;

    @BeforeEach
    public void setup() {
        // Mock telecom provider client responses
        when(telecomProviderClient.verifyPhoneNumber(anyString())).thenReturn(VerificationStatus.MATCH);
        when(telecomProviderClient.getDevicePhoneNumber()).thenReturn("+1234567890");
    }

    @Test
    @WithMockUser
    public void verifyPhoneNumber_shouldReturnMatchResult() throws Exception {
        // Given
        VerificationRequest request = new VerificationRequest();
        request.setPhoneNumber("+1234567890");
        request.setCorrelationId("test-correlation-id");

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
        assertThat(response.getStatus()).isEqualTo(VerificationStatus.MATCH);
        assertThat(response.getVerificationId()).isNotNull();
        assertThat(response.getVerificationTime()).isNotNull();
    }

    @Test
    @WithMockUser
    public void getDevicePhoneNumber_shouldReturnPhoneNumber() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/device-phone-number")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        DevicePhoneNumberResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), DevicePhoneNumberResponse.class);
        
        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(response.getRetrievalTime()).isNotNull();
    }

    @Test
    public void verifyPhoneNumber_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        // Given
        VerificationRequest request = new VerificationRequest();
        request.setPhoneNumber("+1234567890");
        request.setCorrelationId("test-correlation-id");

        // When/Then
        mockMvc.perform(post("/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getDevicePhoneNumber_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        // When/Then
        mockMvc.perform(get("/device-phone-number")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void verifyPhoneNumber_withInvalidPhoneNumber_shouldReturnBadRequest() throws Exception {
        // Given
        VerificationRequest request = new VerificationRequest();
        request.setPhoneNumber("invalid-phone-number");
        request.setCorrelationId("test-correlation-id");

        // When/Then
        mockMvc.perform(post("/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
