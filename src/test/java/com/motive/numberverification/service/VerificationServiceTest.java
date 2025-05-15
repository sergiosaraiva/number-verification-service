package com.motive.numberverification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.motive.numberverification.api.model.DevicePhoneNumberResponse;
import com.motive.numberverification.api.model.VerificationRequest;
import com.motive.numberverification.api.model.VerificationStatus;
import com.motive.numberverification.integration.TelecomProviderClient;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    private TelecomProviderClient telecomProviderClient;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @InjectMocks
    private VerificationService verificationService;

    @BeforeEach
    public void setup() {
        // Mock request context holder
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    public void verifyPhoneNumber_shouldReturnTrue() {
        // Given
        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setPhoneNumber("+1234567890");
        
        // Mock telecom provider responses
        when(telecomProviderClient.verifyPhoneNumber(anyString())).thenReturn(VerificationStatus.MATCH);
        when(telecomProviderClient.getDevicePhoneNumber()).thenReturn("+1234567890");

        // When
        boolean result = verificationService.verifyPhoneNumber(verificationRequest);

        // Then
        assertThat(result).isTrue();
        verify(telecomProviderClient).verifyPhoneNumber("+1234567890");
    }

    @Test
    public void getDevicePhoneNumber_shouldReturnPhoneNumber() {
        // Given
        when(telecomProviderClient.getDevicePhoneNumber()).thenReturn("+1234567890");

        // When
        String phoneNumber = verificationService.getDevicePhoneNumber();

        // Then
        assertThat(phoneNumber).isEqualTo("+1234567890");
        verify(telecomProviderClient).getDevicePhoneNumber();
    }
}