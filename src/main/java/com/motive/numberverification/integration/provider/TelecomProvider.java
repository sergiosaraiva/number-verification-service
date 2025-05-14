package com.motive.numberverification.integration.provider;

import com.motive.numberverification.api.model.VerificationResponse.VerificationStatus;

/**
 * Interface for telecom providers that implement number verification functionality.
 */
public interface TelecomProvider {
    
    /**
     * Verify if the provided phone number matches the user's device.
     * 
     * @param phoneNumber The phone number to verify in E.164 format
     * @return VerificationStatus indicating the result of verification
     */
    VerificationStatus verifyPhoneNumber(String phoneNumber);
    
    /**
     * Retrieve the phone number from the user's device.
     * 
     * @return The phone number in E.164 format
     */
    String getDevicePhoneNumber();
}