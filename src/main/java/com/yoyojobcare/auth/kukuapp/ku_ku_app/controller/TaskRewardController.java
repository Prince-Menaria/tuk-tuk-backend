package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.TaskRewardService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.DailyLoginRewardDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.TaskRewardDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskRewardController {

    private final TaskRewardService taskRewardService;

    /**
     * 📋 Get user's task rewards history (Matches Screenshot 2 & 3: "Details" pages)
     */
    @GetMapping("/rewards")
    public ResponseEntity<MobileResponse<List<TaskRewardDto>>> getTaskRewards(
            @RequestParam Long userId) {
        
        try {
            log.info("📋 Getting task rewards for user: {}", userId);
            
            List<TaskRewardDto> rewards = taskRewardService.getUserTaskRewards(userId);
            
            MobileResponse<List<TaskRewardDto>> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(true);
            mobileResponse.setMessage("Task rewards retrieved successfully");
            mobileResponse.setData(rewards);
            
            return ResponseEntity.ok(mobileResponse);
            
        } catch (Exception e) {
            log.error("❌ Error getting task rewards: {}", e.getMessage());
            
            MobileResponse<List<TaskRewardDto>> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 🎁 Get daily login reward status (To show if user can claim today)
     */
    @GetMapping("/daily-login-status")
    public ResponseEntity<MobileResponse<DailyLoginRewardDto>> getDailyLoginStatus(
            @RequestParam Long userId) {
        try {
            DailyLoginRewardDto response = taskRewardService.getDailyLoginStatus(userId);

            MobileResponse<DailyLoginRewardDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(true);
            mobileResponse.setMessage("Task rewards retrieved successfully");
            mobileResponse.setData(response);
            return ResponseEntity.ok(mobileResponse);
        } catch (Exception e) {
            log.error("Error getting daily login status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 🎁 Claim daily login reward (Matches Screenshot 4: "Golds can be gained by signing in.")
     */
    @PostMapping("/daily-login-claim")
    public ResponseEntity<MobileResponse<CurrencyOperationResponseDto>> claimDailyLoginReward(
            @RequestParam Long userId) {
        
        try {
            log.info("🎁 User {} claiming daily login reward", userId);
            
            CurrencyOperationResponseDto response = taskRewardService.claimDailyLoginReward(userId);
            
            MobileResponse<CurrencyOperationResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(response.getSuccess());
            mobileResponse.setMessage(response.getMessage());
            mobileResponse.setData(response);
            
            return ResponseEntity.ok(mobileResponse);
            
        } catch (Exception e) {
            log.error("❌ Error claiming daily reward: {}", e.getMessage());
            
            MobileResponse<CurrencyOperationResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Example of an internal endpoint to trigger a specific task reward
    // This would typically be called by an internal system (e.g., when a user completes a game level)
    // @PostMapping("/complete-task/{taskIdentifier}")
    // public ResponseEntity<MobileResponse<CurrencyOperationResponseDto>> completeTask(
    //         @AuthenticationPrincipal OAuth2User currentUser, // Can be admin/system user
    //         @RequestParam("targetUserId") Long targetUserId,
    //         @PathVariable String taskIdentifier,
    //         @RequestParam BigDecimal rewardAmount,
    //         @RequestParam CurrencyType currencyType) {
        
    //     try {
    //         // currentUserService.validateAdminOrSystemUser(currentUser); // Ensure only authorized calls
    //         log.info("System triggering task '{}' for user {} with {} {}", taskIdentifier, targetUserId, rewardAmount, currencyType);
    //         CurrencyOperationResponseDto response = taskRewardService.completeSpecificTask(targetUserId, taskIdentifier, rewardAmount, currencyType);
    //         return ResponseEntity.ok(MobileResponse.<CurrencyOperationResponseDto>builder().status(response.getSuccess()).message(response.getMessage()).data(response).build());
    //     } catch (Exception e) {
    //         log.error("Error completing task: {}", e.getMessage());
    //         return ResponseEntity.badRequest().body(MobileResponse.<CurrencyOperationResponseDto>builder().status(false).message(e.getMessage()).build());
    //     }
    // }
}
