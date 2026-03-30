package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponseDto {
    private Boolean success;
    private String message;
    private Long packageId;
    private BigDecimal diamondsPurchased;
    private BigDecimal amountPaid;
    private String currency;
    private String paymentId; // Payment gateway transaction ID
    private String paymentStatus;
    private BigDecimal newDiamondBalance;
    private Long transactionId; // Wallet transaction ID
    private String referenceId;
    private String paymentMethod; // "UPI", "Card", etc.

}
