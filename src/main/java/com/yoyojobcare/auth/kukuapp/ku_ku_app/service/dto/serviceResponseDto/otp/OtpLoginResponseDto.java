package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpLoginResponseDto {
    
    private Boolean success;
    private String message;
    private Boolean isNewUser; // ✅ Naya user hai to profile complete karna padega
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String fullName;
    private String email;
    private String image;
}
