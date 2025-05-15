package com.motive.numberverification.api.model;

import jakarta.validation.constraints.Pattern;

public class VerificationRequest {
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format")
    private String phoneNumber;
    
    private String hashedPhoneNumber;

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getHashedPhoneNumber() {
        return hashedPhoneNumber;
    }
    
    public void setHashedPhoneNumber(String hashedPhoneNumber) {
        this.hashedPhoneNumber = hashedPhoneNumber;
    }
    
    // Validation helper
    public boolean hasValidInput() {
        return (phoneNumber != null && !phoneNumber.isEmpty()) || 
               (hashedPhoneNumber != null && !hashedPhoneNumber.isEmpty());
    }
}