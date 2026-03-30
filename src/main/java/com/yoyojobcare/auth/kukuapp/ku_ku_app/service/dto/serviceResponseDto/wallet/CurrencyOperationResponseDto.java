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
public class CurrencyOperationResponseDto {
    private Boolean success;
    private String message;
    private Long transactionId;
    private String referenceId;
    private LocalDateTime transactionDate;
    private String currencyType;
    private BigDecimal amount;
    private String formattedAmount;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private String sourceType;
    private String sourceDescription;

}
