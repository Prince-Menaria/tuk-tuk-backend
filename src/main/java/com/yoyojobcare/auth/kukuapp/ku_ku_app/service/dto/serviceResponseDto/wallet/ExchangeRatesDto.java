package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRatesDto {
    private BigDecimal diamondToGoldRate; // e.g., 10 (1 diamond = 10 gold)
    private String rateDescription; // e.g., "1 Diamond = 10 Gold"
    private Boolean exchangeEnabled;

}
