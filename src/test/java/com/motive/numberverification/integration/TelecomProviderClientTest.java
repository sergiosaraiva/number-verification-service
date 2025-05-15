package com.motive.numberverification.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.motive.numberverification.api.model.VerificationStatus;
import com.motive.numberverification.integration.provider.TelecomProvider;

@ExtendWith(MockitoExtension.class)
public class TelecomProviderClientTest {

    @Mock
    private TelecomProvider defaultTelecomProvider;

    @Mock
    private TelecomProvider fallbackTelecomProvider;

    @InjectMocks
    private TelecomProviderClient telecomProviderClient;

    private static final String TEST_PHONE_NUMBER = "+1234567890";

    @BeforeEach
    public void setup() {
        // Mock the primary provider
        when(defaultTelecomProvider.verifyPhoneNumber(anyString())).thenReturn(VerificationStatus.MATCH);
        when(defaultTelecomProvider.getDevicePhoneNumber()).thenReturn(TEST_PHONE_NUMBER);
    }

    @Test
    public void verifyPhoneNumber_primaryProviderSuccessful_returnsMatch() {
        // When
        VerificationStatus result = telecomProviderClient.verifyPhoneNumber(TEST_PHONE_NUMBER);

        // Then
        assertThat(result).isEqualTo(VerificationStatus.MATCH);
    }

    @Test
    public void verifyPhoneNumber_primaryProviderFails_usesFallback() {
        // Given
        when(defaultTelecomProvider.verifyPhoneNumber(anyString())).thenThrow(new RuntimeException("Provider unavailable"));
        when(fallbackTelecomProvider.verifyPhoneNumber(anyString())).thenReturn(VerificationStatus.NO_MATCH);

        // When
        VerificationStatus result = telecomProviderClient.verifyPhoneNumber(TEST_PHONE_NUMBER);

        // Then
        assertThat(result).isEqualTo(VerificationStatus.NO_MATCH);
    }

    @Test
    public void verifyPhoneNumber_bothProvidersFail_returnsIndeterminate() {
        // Given
        when(defaultTelecomProvider.verifyPhoneNumber(anyString())).thenThrow(new RuntimeException("Primary provider unavailable"));
        when(fallbackTelecomProvider.verifyPhoneNumber(anyString())).thenThrow(new RuntimeException("Fallback provider unavailable"));

        // When
        VerificationStatus result = telecomProviderClient.verifyPhoneNumber(TEST_PHONE_NUMBER);

        // Then
        assertThat(result).isEqualTo(VerificationStatus.INDETERMINATE);
    }

    @Test
    public void getDevicePhoneNumber_primaryProviderSuccessful_returnsPhoneNumber() {
        // When
        String result = telecomProviderClient.getDevicePhoneNumber();

        // Then
        assertThat(result).isEqualTo(TEST_PHONE_NUMBER);
    }

    @Test
    public void getDevicePhoneNumber_primaryProviderFails_usesFallback() {
        // Given
        when(defaultTelecomProvider.getDevicePhoneNumber()).thenThrow(new RuntimeException("Provider unavailable"));
        when(fallbackTelecomProvider.getDevicePhoneNumber()).thenReturn("+0987654321");

        // When
        String result = telecomProviderClient.getDevicePhoneNumber();

        // Then
        assertThat(result).isEqualTo("+0987654321");
    }

    @Test
    public void getDevicePhoneNumber_bothProvidersFail_returnsNull() {
        // Given
        when(defaultTelecomProvider.getDevicePhoneNumber()).thenThrow(new RuntimeException("Primary provider unavailable"));
        when(fallbackTelecomProvider.getDevicePhoneNumber()).thenThrow(new RuntimeException("Fallback provider unavailable"));

        // When
        String result = telecomProviderClient.getDevicePhoneNumber();

        // Then
        assertThat(result).isNull();
    }
}