package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.store;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AddItemRequestDto {

    private String itemName;

    // ✅ Main category: INTIMACY, CHAT_FRAME, AVATAR_FRAME, ENTER_EFFECT, ROOM_THEME
    private String mainCategory;

    // ✅ Sub category (sirf Intimacy ke liye): FRAME, JEWELLERY, MIC, CAR, BUBBLE, ENTRANCE
    private String subCategory;

    private String itemImage;

    private BigDecimal price;

    // ✅ Currency: DIAMOND, GOLD
    private String currency = "DIAMOND";

    // ✅ Duration: 7, 30, 90, 365 (days), 0 = permanent
    private Integer durationDays = 7;

}
