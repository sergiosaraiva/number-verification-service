 package com.motive.numberverification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

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
import com.motive.numberverification.api.model.VerificationResponse;
import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import com.motive.numberverification.common.metrics.MetricsCollector;
import com.motive.numberverification.common.util.PhoneNumberUtils;
import com.motive.numberverification.integration.TelecomProviderClient;
import com.motive.numberverification.persistence.VerificationLogRepository;
import com.motive.numberverification.persistence.entity.VerificationLogEntity;
import com.motive.numberverification.service.VerificationService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    private TelecomProviderClient telecomProviderClient;

    @Mock
    private VerificationLogRepository verificationLogRepository;

    @Mock
    private MetricsCollector metricsCollector;

    @Mock
    private PhoneNumberUtils phoneNumberUtils;

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
        
        // Mock phone number utils
        when(phoneNumberUtils.normalizePhoneNumber(anyString())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    public void verifyPhoneNumber_shouldReturnMatchResult() {
        // Given
        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setPhoneNumber("+1234567890");
        verificationRequest.setCorrelationId("test-correlation-id");

        when(telecomProviderClient.verifyPhoneNumber(anyString())).thenReturn(VerificationStatus.MATCH);
        when(verificationLogRepository.save(any(VerificationLogEntity.class))).thenAnswer(i -> i.getArgument(0));

        // When
        VerificationResponse response = verificationService.verifyPhoneNumber(verificationRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(VerificationStatus.MATCH);
        assertThat(response.getVerificationId()).isNotNull();
        assertThat(response.getVerificationTime()).isNotNull();

        verify(telecomProviderClient).verifyPhoneNumber("+1234567890");
        verify(verificationLogRepository).save(any(VerificationLogEntity.class));
    }

    @Test
    public void getDevicePhoneNumber_shouldReturnPhoneNumber() {
        // Given
        when(telecomProviderClient.getDevicePhoneNumber()).thenReturn("+1234567890");

        // When
        DevicePhoneNumberResponse response = verificationService.getDevicePhoneNumber();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(response.getRetrievalTime()).isNotNull();

        verify(telecomProviderClient).getDevicePhoneNumber();
    }
}
