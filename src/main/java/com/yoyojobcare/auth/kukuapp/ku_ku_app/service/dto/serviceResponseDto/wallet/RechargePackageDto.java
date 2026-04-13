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
public class RechargePackageDto {
    private Long packageId;
    private String packageName;
    private BigDecimal diamonds;
    private BigDecimal price;
    private String currency;
    private Boolean isPopular;
    private Boolean hasDiscount;
    private String badgeText;
    private String displayDiamonds;
    private String displayPrice;
}
