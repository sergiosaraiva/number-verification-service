package com.motive.numberverification.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for collecting application metrics.
 */
@Component
public class MetricsCollector {

    private final MeterRegistry meterRegistry;
    
    // API metrics
    private final Counter verifyRequestCounter;
    private final Counter devicePhoneNumberRequestCounter;
    private final Timer verifyRequestTimer;
    private final Timer devicePhoneNumberRequestTimer;
    
    // Provider metrics
    private final Counter primaryProviderSuccessCounter;
    private final Counter primaryProviderFailureCounter;
    private final Counter fallbackProviderUsedCounter;
    private final Timer primaryProviderResponseTimer;
    
    // Business metrics
    private final Counter matchCounter;
    private final Counter noMatchCounter;
    private final Counter indeterminateCounter;
    
    public MetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize API metrics
        this.verifyRequestCounter = Counter.builder("api.verify.requests")
                .description("Number of verify requests received")
                .register(meterRegistry);
        
        this.devicePhoneNumberRequestCounter = Counter.builder("api.device_phone_number.requests")
                .description("Number of device phone number requests received")
                .register(meterRegistry);
        
        this.verifyRequestTimer = Timer.builder("api.verify.duration")
                .description("Time taken to process verify requests")
                .register(meterRegistry);
        
        this.devicePhoneNumberRequestTimer = Timer.builder("api.device_phone_number.duration")
                .description("Time taken to process device phone number requests")
                .register(meterRegistry);
        
        // Initialize provider metrics
        this.primaryProviderSuccessCounter = Counter.builder("provider.primary.success")
                .description("Number of successful primary provider calls")
                .register(meterRegistry);
        
        this.primaryProviderFailureCounter = Counter.builder("provider.primary.failure")
                .description("Number of failed primary provider calls")
                .register(meterRegistry);
        
        this.fallbackProviderUsedCounter = Counter.builder("provider.fallback.used")
                .description("Number of times fallback provider was used")
                .register(meterRegistry);
        
        this.primaryProviderResponseTimer = Timer.builder("provider.primary.duration")
                .description("Time taken by primary provider to respond")
                .register(meterRegistry);
        
        // Initialize business metrics
        this.matchCounter = Counter.builder("verification.result.match")
                .description("Number of verification matches")
                .register(meterRegistry);
        
        this.noMatchCounter = Counter.builder("verification.result.no_match")
                .description("Number of verification non-matches")
                .register(meterRegistry);
        
        this.indeterminateCounter = Counter.builder("verification.result.indeterminate")
                .description("Number of indeterminate verification results")
                .register(meterRegistry);
    }
    
    // API metrics methods
    
    public void incrementVerifyRequestCounter() {
        verifyRequestCounter.increment();
    }
    
    public void incrementDevicePhoneNumberRequestCounter() {
        devicePhoneNumberRequestCounter.increment();
    }
    
    public Timer.Sample startVerifyRequestTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void stopVerifyRequestTimer(Timer.Sample sample) {
        sample.stop(verifyRequestTimer);
    }
    
    public Timer.Sample startDevicePhoneNumberRequestTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void stopDevicePhoneNumberRequestTimer(Timer.Sample sample) {
        sample.stop(devicePhoneNumberRequestTimer);
    }
    
    // Provider metrics methods
    
    public void incrementPrimaryProviderSuccessCounter() {
        primaryProviderSuccessCounter.increment();
    }
    
    public void incrementPrimaryProviderFailureCounter() {
        primaryProviderFailureCounter.increment();
    }
    
    public void incrementFallbackProviderUsedCounter() {
        fallbackProviderUsedCounter.increment();
    }
    
    public Timer.Sample startPrimaryProviderResponseTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void stopPrimaryProviderResponseTimer(Timer.Sample sample) {
        sample.stop(primaryProviderResponseTimer);
    }
    
    public void recordPrimaryProviderResponseTime(long timeInMillis) {
        primaryProviderResponseTimer.record(timeInMillis, TimeUnit.MILLISECONDS);
    }
    
    // Business metrics methods
    
    public void incrementMatchCounter() {
        matchCounter.increment();
    }
    
    public void incrementNoMatchCounter() {
        noMatchCounter.increment();
    }
    
    public void incrementIndeterminateCounter() {
        indeterminateCounter.increment();
    }
}
