package com.motive.numberverification.api.model;

import java.time.Instant;

public class DevicePhoneNumberResponse {

    private String phoneNumber;
    private Instant retrievalTime;

    // Constructors
    public DevicePhoneNumberResponse() {
    }

    public DevicePhoneNumberResponse(String phoneNumber, Instant retrievalTime) {
        this.phoneNumber = phoneNumber;
        this.retrievalTime = retrievalTime;
    }

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getRetrievalTime() {
        return retrievalTime;
    }

    public void setRetrievalTime(Instant retrievalTime) {
        this.retrievalTime = retrievalTime;
    }
}
