package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemResponseDto {

    private Boolean success;
    private String message;
    private Long inventoryId;
    private String itemName;
    private BigDecimal pricePaid;
    private String currencyUsed;
    private BigDecimal remainingDiamonds;
    private BigDecimal remainingGold;
    private LocalDateTime expiresAt;

}
