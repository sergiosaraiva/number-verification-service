package com.motive.numberverification.service;

import com.motive.numberverification.api.model.DevicePhoneNumberResponse;
import com.motive.numberverification.api.model.VerificationRequest;
import com.motive.numberverification.api.model.VerificationResponse;
import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;
import com.motive.numberverification.integration.TelecomProviderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Simplified service implementation for testing
 */
@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);
    
    private final TelecomProviderClient telecomProviderClient;
    
    public VerificationService(TelecomProviderClient telecomProviderClient) {
        this.telecomProviderClient = telecomProviderClient;
    }
    
    /**
     * Verify if the provided phone number matches the user's device.
     */
    public VerificationResponse verifyPhoneNumber(VerificationRequest request) {
        logger.info("Processing verification for phone number");
        
        try {
            // Generate a unique verification ID
            String verificationId = UUID.randomUUID().toString();
            
            // Call the telecom provider to verify the phone number
            VerificationStatus status = telecomProviderClient.verifyPhoneNumber(request.getPhoneNumber());
            
            // Create verification response
            VerificationResponse response = new VerificationResponse(
                    verificationId,
                    status,
                    Instant.now()
            );
            
            logger.info("Verification completed with status: {}", status);
            
            return response;
        } catch (Exception e) {
            logger.error("Error during verification: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Retrieve the phone number from the user's device.
     */
    public DevicePhoneNumberResponse getDevicePhoneNumber() {
        logger.info("Retrieving device phone number");
        
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
        } catch (Exception e) {
            logger.error("Error retrieving device phone number: {}", e.getMessage(), e);
            throw e;
        }
    }
}