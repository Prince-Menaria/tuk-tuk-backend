package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddGiftRequestDto {

    private String giftName;

    private String giftImage;

    private BigDecimal diamondCost;

    private String category; // POPULAR, LUXURY, SPECIAL, RANDOM

    private Boolean isActive = true;

    private Integer orderIndex;
    private String animationUrl; // Gift animation

}
