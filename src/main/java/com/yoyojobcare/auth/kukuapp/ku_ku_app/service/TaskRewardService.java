package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.math.BigDecimal;
import java.util.List;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.DailyLoginRewardDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.TaskRewardDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;

public interface TaskRewardService {
    List<TaskRewardDto> getUserTaskRewards(Long userId); // All task rewards

    CurrencyOperationResponseDto claimDailyLoginReward(Long userId); // Screenshot 4 "signing in"

    DailyLoginRewardDto getDailyLoginStatus(Long userId); // To show if user can claim

    // Example of another task reward
    CurrencyOperationResponseDto completeSpecificTask(Long userId, String taskIdentifier, BigDecimal rewardAmount,
            CurrencyType currencyType);

}
