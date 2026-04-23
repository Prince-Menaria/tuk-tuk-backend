package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDto {

    private Long inventoryId;
    private Long itemId;
    private String itemName;
    private String mainCategory;
    private String subCategory;
    private String itemImage;
    private Boolean isEquipped;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
    private Boolean isExpired;
    private String durationLabel;

}
