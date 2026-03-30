package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLoginRewardDto {
    private Boolean canClaimToday;
    private BigDecimal rewardAmountToday;
    private String rewardCurrencyToday;
    private Integer consecutiveDays;
    private LocalDate lastClaimDate;
    private String message;

}
