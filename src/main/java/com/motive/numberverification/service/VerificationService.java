package com.motive.numberverification.service;

import com.motive.numberverification.api.model.VerificationRequest;
import com.motive.numberverification.api.model.VerificationStatus;
import com.motive.numberverification.integration.TelecomProviderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

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
    public boolean verifyPhoneNumber(VerificationRequest request) {
        logger.info("Processing verification request");
        
        try {
            // Get the device phone number from telecom provider
            String devicePhoneNumber = telecomProviderClient.getDevicePhoneNumber();
            
            // If the request contains a plain phone number
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
                // Verify with the telecom provider
                VerificationStatus status = telecomProviderClient.verifyPhoneNumber(request.getPhoneNumber());
                return status == VerificationStatus.MATCH;
            }
            
            // If the request contains a hashed phone number
            if (request.getHashedPhoneNumber() != null && !request.getHashedPhoneNumber().isEmpty()) {
                String hashedDeviceNumber = hashPhoneNumber(devicePhoneNumber);
                return hashedDeviceNumber.equals(request.getHashedPhoneNumber());
            }
            
            // No valid input provided
            logger.warn("No valid phone number provided in the request");
            return false;
            
        } catch (Exception e) {
            logger.error("Error during verification: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Retrieve the phone number from the user's device.
     */
    public String getDevicePhoneNumber() {
        logger.info("Retrieving device phone number");
        
        try {
            // Call the telecom provider to retrieve the device phone number
            String phoneNumber = telecomProviderClient.getDevicePhoneNumber();
            
            logger.info("Device phone number retrieved successfully");
            
            return phoneNumber;
        } catch (Exception e) {
            logger.error("Error retrieving device phone number: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Hash a phone number using SHA-256.
     */
    private String hashPhoneNumber(String phoneNumber) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(phoneNumber.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing phone number: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to hash phone number", e);
        }
    }
}