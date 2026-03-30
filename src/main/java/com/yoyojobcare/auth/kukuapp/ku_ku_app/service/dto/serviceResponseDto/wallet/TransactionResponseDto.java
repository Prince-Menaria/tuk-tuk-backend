package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {
    private Long transactionId;
    private String referenceId;
    private String currencyType;
    private String currencyIcon;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String sourceType;
    private String sourceDescription;
    private LocalDateTime transactionDate;
    private String status;
    private String formattedAmount; // "+100 💎" or "-50 🥇"

}
