package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpResponseDto {
    private Boolean success;
    private String message;
    private String mobile;
    private Integer expiresInSeconds;
}
