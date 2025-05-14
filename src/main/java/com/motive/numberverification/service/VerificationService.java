package com.motive.numberverification.service;

import com.motive.numberverification.api.model.DevicePhoneNumberResponse;
import com.motive.numberverification.api.model.VerificationRequest;
import com.motive.numberverification.api.model.VerificationResponse;
import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import com.motive.numberverification.common.util.PhoneNumberUtils;
import com.motive.numberverification.integration.TelecomProviderClient;
import com.motive.numberverification.persistence.VerificationLogRepository;
import com.motive.numberverification.persistence.entity.VerificationLogEntity;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;
import java.time.Duration;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);
    
    private final TelecomProviderClient telecomProviderClient;
    private final VerificationLogRepository verificationLogRepository;
    private final MeterRegistry meterRegistry;
    private final PhoneNumberUtils phoneNumberUtils;
    
    @Autowired
    public VerificationService(
            TelecomProviderClient telecomProviderClient,
            VerificationLogRepository verificationLogRepository,
            MeterRegistry meterRegistry,
            PhoneNumberUtils phoneNumberUtils) {
        this.telecomProviderClient = telecomProviderClient;
        this.verificationLogRepository = verificationLogRepository;
        this.meterRegistry = meterRegistry;
        this.phoneNumberUtils = phoneNumberUtils;
    }
    
    /**
     * Verify if the provided phone number matches the user's device.
     * 
     * @param request The verification request containing the phone number
     * @return VerificationResponse with the result of verification
     */
    public VerificationResponse verifyPhoneNumber(VerificationRequest request) {
        logger.info("Processing verification for phone number");
        
        // Increment verification request counter
        meterRegistry.counter("verification_requests_total").increment();
        
        // Start timing the verification process
        long startTime = System.nanoTime();
        
        try {
            // Ensure phone number is in E.164 format
            String normalizedNumber = phoneNumberUtils.normalizePhoneNumber(request.getPhoneNumber());
            
            // Generate a unique verification ID
            String verificationId = UUID.randomUUID().toString();
            
            // Call the telecom provider to verify the phone number
            VerificationStatus status = telecomProviderClient.verifyPhoneNumber(normalizedNumber);
            
            // Create verification response
            VerificationResponse response = new VerificationResponse(
                    verificationId,
                    status,
                    Instant.now()
            );
            
            // Record verification status in metrics
            meterRegistry.counter("verification_status", "status", status.name()).increment();
            
            // Log the verification transaction
            logVerification(verificationId, normalizedNumber, status.name(), request.getCorrelationId());
            
            logger.info("Verification completed with status: {}", status);
            
            return response;
        } finally {
            // Record the verification duration
            long duration = System.nanoTime() - startTime;
            meterRegistry.timer("verification_duration_seconds").record(Duration.ofNanos(duration));
        }
    }
    
    /**
     * Retrieve the phone number from the user's device.
     * Result is cached to improve performance for repeated requests.
     * 
     * @return DevicePhoneNumberResponse containing the phone number
     */
    @Cacheable(value = "devicePhoneNumbers", key = "#root.methodName")
    public DevicePhoneNumberResponse getDevicePhoneNumber() {
        logger.info("Retrieving device phone number");
        
        // Increment device phone number request counter
        meterRegistry.counter("device_phone_number_requests_total").increment();
        
        // Start timing the retrieval process
        long startTime = System.nanoTime();
        
        try {
            // Call the telecom provider to retrieve the device phone number
            String phoneNumber = telecomProviderClient.getDevicePhoneNumber();
            
            // Create response
            DevicePhoneNumberResponse response = new DevicePhoneNumberResponse(
                    phoneNumber,
                    Instant.now()
            );
            
            logger.info("Device phone number retrieved successfully");
            
            return response;
        } finally {
            // Record the retrieval duration
            long duration = System.nanoTime() - startTime;
            meterRegistry.timer("verification_duration_seconds").record(Duration.ofNanos(duration));
        }
    }
    
    /**
     * Log the verification transaction to the database for audit purposes.
     */
    private void logVerification(String verificationId, String phoneNumber, String status, String correlationId) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String clientIp = request.getRemoteAddr();
            
            VerificationLogEntity logEntity = VerificationLogEntity.builder()
                    .verificationId(verificationId)
                    .phoneNumber(phoneNumber)
                    .status(status)
                    .correlationId(correlationId)
                    .timestamp(Instant.now())
                    .clientIp(clientIp)
                    .provider("PRIMARY") // This would be dynamically determined in a real implementation
                    .build();
            
            verificationLogRepository.save(logEntity);
            
            logger.debug("Verification log saved to database");
        } catch (Exception e) {
            logger.error("Failed to save verification log: {}", e.getMessage());
            // We don't want to fail the verification if logging fails
        }
    }
}
