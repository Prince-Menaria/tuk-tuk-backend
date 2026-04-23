package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreItemResponseDto {

    private Long itemId;
    private String itemName;
    private String mainCategory;
    private String subCategory;
    private String itemImage;
    private BigDecimal price;
    private String currency;
    private Integer durationDays;
    private String durationLabel;  // "7 Days", "Permanent"
    private String level;
    private Boolean isOwned;       // ✅ User ne kharida hai?
    private Boolean isEquipped;    // ✅ Equipped hai?
}


