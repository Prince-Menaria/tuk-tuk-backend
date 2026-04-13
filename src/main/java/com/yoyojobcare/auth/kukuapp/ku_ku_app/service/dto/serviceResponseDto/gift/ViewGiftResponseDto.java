package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewGiftResponseDto {

    private Long giftId;
    private String giftName;
    private String giftImage;
    private BigDecimal diamondCost;
    private String category;
    private String animationUrl;
    private Integer orderIndex;

}
