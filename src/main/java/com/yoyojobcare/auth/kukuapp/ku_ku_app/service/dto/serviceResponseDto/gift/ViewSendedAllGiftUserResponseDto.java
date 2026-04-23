package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewSendedAllGiftUserResponseDto {

    private Long giftId;
    private String giftName;
    private String giftImage;

}
