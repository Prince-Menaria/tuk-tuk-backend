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
public class WalletBalanceResponseDto {
    private Long userId;
    private String userName;
    private BigDecimal diamonds;
    private BigDecimal gold;
    private BigDecimal totalDiamondsEarned;
    private BigDecimal totalGoldEarned;
    private String walletStatus;
    private LocalDateTime lastUpdated;

}
