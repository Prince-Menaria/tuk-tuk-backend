package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet;

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
    private String paymentId;
    private String paymentStatus;
    private BigDecimal newDiamondBalance;
    private Long transactionId;
    private String referenceId;
}
