package com.motive.numberverification.api;

import com.motive.numberverification.api.model.DevicePhoneNumberResponse;
import com.motive.numberverification.api.model.VerificationRequest;
import com.motive.numberverification.api.model.VerificationResponse;
import com.motive.numberverification.service.VerificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Number Verification", description = "Number Verification API")
public class VerificationController {

    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);
    
    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify if provided phone number matches the user's device",
               description = "Validates whether the provided phone number matches the one associated with the user's device")
    public ResponseEntity<VerificationResponse> verifyPhoneNumber(@Valid @RequestBody VerificationRequest request) {
        logger.info("Received verification request");
        
        if (request.getPhoneNumber() != null) {
            logger.info("Verifying phone number: {}", maskPhoneNumber(request.getPhoneNumber()));
        } else if (request.getHashedPhoneNumber() != null) {
            logger.info("Verifying hashed phone number");
        }
        
        // Call the service layer to process the verification
        boolean verified = verificationService.verifyPhoneNumber(request);
        
        // Create response with verification result
        VerificationResponse response = new VerificationResponse(verified);
        
        logger.info("Verification completed with result: {}", verified);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/device-phone-number")
    @Operation(summary = "Retrieve phone number from user's device",
               description = "Retrieves the phone number associated with the user's device")
    public ResponseEntity<DevicePhoneNumberResponse> getDevicePhoneNumber() {
        logger.info("Received request to retrieve device phone number");
        
        // Call the service layer to retrieve the device phone number
        String phoneNumber = verificationService.getDevicePhoneNumber();
        
        // Create response with phone number
        DevicePhoneNumberResponse response = new DevicePhoneNumberResponse(phoneNumber);
        
        logger.info("Retrieved device phone number: {}", maskPhoneNumber(phoneNumber));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Masks a phone number for logging purposes.
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 8) {
            return "INVALID_NUMBER";
        }
        
        int visibleDigits = 4;
        int prefixLength = 3; // +XX
        
        StringBuilder masked = new StringBuilder();
        masked.append(phoneNumber.substring(0, prefixLength));
        
        for (int i = prefixLength; i < phoneNumber.length() - visibleDigits; i++) {
            masked.append("*");
        }
        
        masked.append(phoneNumber.substring(phoneNumber.length() - visibleDigits));
        
        return masked.toString();
    }
}