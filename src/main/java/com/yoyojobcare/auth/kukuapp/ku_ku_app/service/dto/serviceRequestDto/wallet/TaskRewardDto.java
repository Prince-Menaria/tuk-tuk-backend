package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRewardDto {
    private Long rewardId; // Internal ID, e.g., DailyReward.id
    private String taskType; // "Task rewards" or "Daily Login Bonus"
    private String rewardAmount; // e.g., "+5", "+30"
    private String currencyType; // "GOLD", "DIAMONDS"
    private String currencyIcon; // "🥇", "💎"
    private LocalDateTime rewardDate;
    private String formattedDate; // "3/21/2026 9:03 PM" (as in screenshot)
    private String timeAgo; // "2 hours ago"
    private String displayText; // "Task rewards +5🥇"
    private String displayAmount; // "+5"

}
