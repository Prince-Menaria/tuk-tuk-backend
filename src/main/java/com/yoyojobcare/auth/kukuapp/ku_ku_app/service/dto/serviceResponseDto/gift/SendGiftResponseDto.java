package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift;

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
public class SendGiftResponseDto {
    
    private Boolean success;
    private String message;
    private Long giftTransactionId;
    private Long giftId;
    private String giftName;
    private String giftImage;
    private String animationUrl;
    private Long senderId;
    private String senderName;
    private String senderImage;
    private Long receiverId;
    private String receiverName;
    private Integer quantity;
    private BigDecimal totalDiamondsCost;
    private BigDecimal senderRemainingDiamonds;
    private LocalDateTime sentAt;
}
