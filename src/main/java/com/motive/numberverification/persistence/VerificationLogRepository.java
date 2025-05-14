package com.motive.numberverification.persistence;

import com.motive.numberverification.persistence.entity.VerificationLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VerificationLogRepository extends MongoRepository<VerificationLogEntity, String> {
    
    List<VerificationLogEntity> findByPhoneNumber(String phoneNumber);
    
    List<VerificationLogEntity> findByStatusAndTimestampBetween(String status, Instant start, Instant end);
    
    List<VerificationLogEntity> findByCorrelationId(String correlationId);
}
