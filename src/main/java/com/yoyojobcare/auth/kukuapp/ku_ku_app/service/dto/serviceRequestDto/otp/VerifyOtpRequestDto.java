package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.otp;

import lombok.Data;

@Data
public class VerifyOtpRequestDto {
    private String mobile;
    private String countryCode;
    private String otpCode;
}
