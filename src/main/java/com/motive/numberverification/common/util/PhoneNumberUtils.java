package com.motive.numberverification.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.motive.numberverification.common.exception.ApiException;

/**
 * Utility class for phone number operations.
 */
@Component
public class PhoneNumberUtils {

    private static final Logger logger = LoggerFactory.getLogger(PhoneNumberUtils.class);
    
    private final PhoneNumberUtil phoneNumberUtil;
    
    public PhoneNumberUtils() {
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
    }
    
    /**
     * Normalize a phone number to E.164 format.
     * 
     * @param phoneNumber The phone number to normalize
     * @return The normalized phone number in E.164 format
     * @throws ApiException if the phone number is invalid
     */
    public String normalizePhoneNumber(String phoneNumber) {
        try {
            // If the phone number is already in E.164 format, validate and return it
            if (phoneNumber.startsWith("+")) {
                PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
                if (phoneNumberUtil.isValidNumber(parsedNumber)) {
                    return phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                } else {
                    throw new ApiException("Invalid phone number format");
                }
            }
            
            // Try to parse with default region code (US)
            PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, "US");
            if (phoneNumberUtil.isValidNumber(parsedNumber)) {
                return phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            } else {
                throw new ApiException("Invalid phone number format");
            }
        } catch (NumberParseException e) {
            logger.error("Failed to parse phone number: {}", e.getMessage());
            throw new ApiException("Invalid phone number format");
        }
    }
    
    /**
     * Check if a phone number is valid.
     * 
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        try {
            PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }
    
    /**
     * Mask a phone number for display or logging purposes.
     * Example: +1234567890 -> +1****7890
     * 
     * @param phoneNumber The phone number to mask
     * @return The masked phone number
     */
    public String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 8) {
            return "INVALID_NUMBER";
        }
        
        int visibleDigits = 4;
        int prefixLength = Math.min(3, phoneNumber.length() - visibleDigits);
        
        StringBuilder masked = new StringBuilder();
        masked.append(phoneNumber.substring(0, prefixLength));
        
        for (int i = prefixLength; i < phoneNumber.length() - visibleDigits; i++) {
            masked.append("*");
        }
        
        masked.append(phoneNumber.substring(phoneNumber.length() - visibleDigits));
        
        return masked.toString();
    }
}