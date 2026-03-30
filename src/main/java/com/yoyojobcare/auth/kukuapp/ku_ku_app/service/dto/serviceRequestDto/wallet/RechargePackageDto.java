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
public class RechargePackageDto {
    private Long packageId;
    private String packageName;
    private BigDecimal diamonds; // e.g., 3600 (for 3.6K)
    private BigDecimal price; // e.g., 56.00 (INR)
    private String currency; // "INR"
    private Boolean isPopular;
    private Boolean hasDiscount;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private String badgeText; // e.g., "+24.0K" bonus diamonds, "Popular"
    private String displayDiamonds; // Formatted string: "3.6K", "36.0K"
    private String displayPrice; // Formatted string: "INR 56"

}
