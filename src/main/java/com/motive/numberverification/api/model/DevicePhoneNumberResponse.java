package com.motive.numberverification.api.model;

public class DevicePhoneNumberResponse {
    private String phoneNumber;

    public DevicePhoneNumberResponse() {
    }

    public DevicePhoneNumberResponse(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}