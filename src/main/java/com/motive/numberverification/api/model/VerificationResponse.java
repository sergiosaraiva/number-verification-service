package com.motive.numberverification.api.model;

import java.time.Instant;

public class VerificationResponse {

    private String verificationId;
    private VerificationStatus status;
    private Instant verificationTime;

    public enum VerificationStatus {
        MATCH,
        NO_MATCH,
        INDETERMINATE
    }

    // Constructors
    public VerificationResponse() {
    }

    public VerificationResponse(String verificationId, VerificationStatus status, Instant verificationTime) {
        this.verificationId = verificationId;
        this.status = status;
        this.verificationTime = verificationTime;
    }

    // Getters and setters
    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public Instant getVerificationTime() {
        return verificationTime;
    }

    public void setVerificationTime(Instant verificationTime) {
        this.verificationTime = verificationTime;
    }
}
