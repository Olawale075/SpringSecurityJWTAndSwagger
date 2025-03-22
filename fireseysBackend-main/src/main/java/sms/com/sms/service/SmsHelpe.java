package sms.com.sms.service;

import org.springframework.stereotype.Component;

@Component
public class SmsHelpe {

    public String sendOtp(String phoneNumber, String otp) {
        // Common logic for sending OTP
        return "OTP sent to: " + phoneNumber;
    }
}

