package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRequestDto {
    // @NotNull(message = "Diamond amount is required")
    // @DecimalMin(value = "1.0", message = "Minimum 1 diamond required")
    private BigDecimal diamondAmount;

}
