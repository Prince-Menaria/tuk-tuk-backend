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
public class ExchangeResponseDto {
    private Boolean success;
    private String message;
    private BigDecimal diamondsExchanged;
    private BigDecimal goldReceived;
    private BigDecimal exchangeRate;
    private BigDecimal remainingDiamonds;
    private BigDecimal newGoldBalance;
    private Long transactionId;
    private String referenceId;
}
