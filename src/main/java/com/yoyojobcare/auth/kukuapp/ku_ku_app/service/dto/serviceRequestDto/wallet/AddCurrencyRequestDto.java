package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCurrencyRequestDto {

    // @NotNull(message = "Currency type is required")
    private String currencyType; // "DIAMONDS" or "GOLD"

    // @NotNull(message = "Amount is required")
    // @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    // @NotBlank(message = "Source type is required")
    private String sourceType;

    private String sourceDescription;
}
