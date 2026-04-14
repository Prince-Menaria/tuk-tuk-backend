package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendGiftRequestDto {

    private Long senderId;    // Means HostId or UserId
    private List<Long> receiverIds;
    private Long giftId;
    private Long roomId;
    private Integer quantity;
}
