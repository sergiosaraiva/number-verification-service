package com.motive.numberverification.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "verification_logs")
public class VerificationLogEntity {

    @Id
    private String id;
    
    private String verificationId;
    private String phoneNumber;
    private String status;
    private String correlationId;
    private Instant timestamp;
    private String clientIp;
    private String provider;

    // Constructors
    public VerificationLogEntity() {
    }

    // Builder pattern for easy construction
    public static class Builder {
        private VerificationLogEntity entity = new VerificationLogEntity();
        
        public Builder verificationId(String verificationId) {
            entity.setVerificationId(verificationId);
            return this;
        }
        
        public Builder phoneNumber(String phoneNumber) {
            entity.setPhoneNumber(phoneNumber);
            return this;
        }
        
        public Builder status(String status) {
            entity.setStatus(status);
            return this;
        }
        
        public Builder correlationId(String correlationId) {
            entity.setCorrelationId(correlationId);
            return this;
        }
        
        public Builder timestamp(Instant timestamp) {
            entity.setTimestamp(timestamp);
            return this;
        }
        
        public Builder clientIp(String clientIp) {
            entity.setClientIp(clientIp);
            return this;
        }
        
        public Builder provider(String provider) {
            entity.setProvider(provider);
            return this;
        }
        
        public VerificationLogEntity build() {
            return entity;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}