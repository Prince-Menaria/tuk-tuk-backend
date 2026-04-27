package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.otp;

import lombok.Data;

@Data
public class SendOtpRequestDto {
    private String mobile;       // "9876543210"
    private String countryCode;  // "+91"
}
