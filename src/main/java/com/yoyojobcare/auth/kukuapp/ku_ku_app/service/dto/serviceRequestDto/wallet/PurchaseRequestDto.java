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
    private Long packageId;
    private String paymentMethod; // "UPI", "Card", etc.
    private String couponCode; // Optional

}
