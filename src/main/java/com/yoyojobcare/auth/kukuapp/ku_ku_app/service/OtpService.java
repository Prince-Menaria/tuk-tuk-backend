package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.otp.SendOtpRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.otp.VerifyOtpRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.otp.OtpLoginResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.otp.SendOtpResponseDto;

public interface OtpService {

    SendOtpResponseDto sendOtp(SendOtpRequestDto request);

    OtpLoginResponseDto verifyOtpAndLogin(VerifyOtpRequestDto request);

}
