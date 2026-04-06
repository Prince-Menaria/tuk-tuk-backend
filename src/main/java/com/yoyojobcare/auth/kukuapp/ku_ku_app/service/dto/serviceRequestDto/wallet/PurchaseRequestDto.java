package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestDto {

    // @NotNull(message = "Package ID is required")
    private Long packageId;

    // @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String couponCode;

}
