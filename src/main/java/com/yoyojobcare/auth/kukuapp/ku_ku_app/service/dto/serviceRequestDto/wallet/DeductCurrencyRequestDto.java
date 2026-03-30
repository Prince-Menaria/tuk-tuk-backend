package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductCurrencyRequestDto {

    private String currencyType; // "DIAMONDS" or "GOLD"

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String sourceType; // "GIFT_SENT", "PURCHASE", etc.
    private String sourceDescription;

}
