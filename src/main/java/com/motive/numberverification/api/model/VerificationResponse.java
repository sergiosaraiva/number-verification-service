package com.motive.numberverification.api.model;

public class VerificationResponse {
    private boolean devicePhoneNumberVerified;

    public VerificationResponse() {
    }

    public VerificationResponse(boolean devicePhoneNumberVerified) {
        this.devicePhoneNumberVerified = devicePhoneNumberVerified;
    }

    public boolean isDevicePhoneNumberVerified() {
        return devicePhoneNumberVerified;
    }

    public void setDevicePhoneNumberVerified(boolean devicePhoneNumberVerified) {
        this.devicePhoneNumberVerified = devicePhoneNumberVerified;
    }
}