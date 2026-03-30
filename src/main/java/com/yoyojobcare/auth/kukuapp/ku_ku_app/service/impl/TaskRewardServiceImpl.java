package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.DailyReward;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.WalletTransaction;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.DailyRewardRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.WalletTransactionRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.TaskRewardService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.WalletService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.DailyLoginRewardDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.TaskRewardDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskRewardServiceImpl implements TaskRewardService {

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;
    private final DailyRewardRepository dailyRewardRepository;
    private final UserRepository userRepository; // Assuming UserRepository exists

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");

    // --- Daily Login Reward Configuration (Can be externalized) ---
    private static final BigDecimal DAILY_LOGIN_GOLD_AMOUNT = new BigDecimal("5"); // Screenshot 2
    private static final BigDecimal DAILY_LOGIN_DIAMOND_AMOUNT = new BigDecimal("1"); // Example for longer streaks

    @Override
    @Transactional(readOnly = true)
    public List<TaskRewardDto> getUserTaskRewards(Long userId) {
        // Fetch all transactions related to task rewards (simulated based on screenshots)
        // In a real app, this might involve a dedicated TaskReward entity or filtering WalletTransactions
        // For now, let's pull all "TASK_REWARD" and "DAILY_LOGIN" transactions
        List<WalletTransaction> taskTransactions = transactionRepository
            .findByUserIdAndSourceTypeOrderByTransactionDateDesc(userId, TransactionSource.TASK_REWARD);
        
        List<WalletTransaction> dailyLoginTransactions = transactionRepository
            .findByUserIdAndSourceTypeOrderByTransactionDateDesc(userId, TransactionSource.DAILY_LOGIN);

        List<WalletTransaction> allRelevantTransactions = new ArrayList<>();
        allRelevantTransactions.addAll(taskTransactions);
        allRelevantTransactions.addAll(dailyLoginTransactions);
        
        // Sort by date (descending) as seen in screenshots
        allRelevantTransactions.sort(Comparator.comparing(WalletTransaction::getTransactionDate).reversed());

        return allRelevantTransactions.stream()
            .map(this::convertToTaskRewardDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CurrencyOperationResponseDto claimDailyLoginReward(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        LocalDate today = LocalDate.now();
        DailyReward lastReward = dailyRewardRepository.findLastClaimedRewardByUserId(userId).orElse(null);

        // Check if already claimed today
        if (dailyRewardRepository.findByUserIdAndClaimDate(userId, today).isPresent()) {
            throw new RuntimeException("Daily login reward already claimed today.");
        }

        int consecutiveDays = 1;
        if (lastReward != null && lastReward.getClaimDate().isEqual(today.minusDays(1))) {
            consecutiveDays = lastReward.getConsecutiveDays() + 1;
        }

        BigDecimal rewardAmount = DAILY_LOGIN_GOLD_AMOUNT; // Default to gold
        CurrencyType rewardCurrency = CurrencyType.GOLD;

        // Example: Reward diamonds after 7 consecutive days
        if (consecutiveDays >= 7) {
            rewardAmount = DAILY_LOGIN_DIAMOND_AMOUNT; // e.g., 1 Diamond
            rewardCurrency = CurrencyType.DIAMONDS;
        } else if (consecutiveDays >= 3) {
            rewardAmount = DAILY_LOGIN_GOLD_AMOUNT.multiply(new BigDecimal("2")); // e.g., 10 Gold
        }

        // Add reward to wallet
        AddCurrencyRequestDto addRequest = AddCurrencyRequestDto.builder()
            .currencyType(rewardCurrency.name())
            .amount(rewardAmount)
            .sourceType(TransactionSource.DAILY_LOGIN.name())
            .sourceDescription("Daily login bonus for " + consecutiveDays + " consecutive days")
            .build();

        CurrencyOperationResponseDto walletResult = walletService.addCurrency(userId, addRequest);

        // Save daily reward record
        DailyReward dailyReward = DailyReward.builder()
            .user(user)
            .claimDate(today)
            .consecutiveDays(consecutiveDays)
            .claimedAt(LocalDateTime.now())
            .rewardCurrencyType(rewardCurrency)
            .rewardAmount(rewardAmount)
            .transactionId(walletResult.getTransactionId())
            .build();
        dailyRewardRepository.save(dailyReward);

        walletResult.setMessage("Daily login reward claimed successfully! Received " + rewardAmount.stripTrailingZeros().toPlainString() + " " + rewardCurrency.name());
        return walletResult;
    }

    @Override
    @Transactional(readOnly = true)
    public DailyLoginRewardDto getDailyLoginStatus(Long userId) {
        LocalDate today = LocalDate.now();
        boolean canClaimToday = !dailyRewardRepository.findByUserIdAndClaimDate(userId, today).isPresent();

        DailyReward lastReward = dailyRewardRepository.findLastClaimedRewardByUserId(userId).orElse(null);

        Integer consecutiveDays = 0;
        if (lastReward != null) {
            consecutiveDays = lastReward.getConsecutiveDays();
            if (!lastReward.getClaimDate().isEqual(today.minusDays(1)) && !lastReward.getClaimDate().isEqual(today)) {
                // Streak broken or it's not today's claim
                consecutiveDays = 0;
            } else if (lastReward.getClaimDate().isEqual(today)) {
                // Already claimed today, consecutive days are as of today's claim
                // No change.
            } else { // lastReward.getClaimDate().isEqual(today.minusDays(1))
                // Can claim today to continue streak
                consecutiveDays += 1;
            }
        }
        
        BigDecimal rewardAmount = DAILY_LOGIN_GOLD_AMOUNT;
        String rewardCurrency = CurrencyType.GOLD.name();
        if (consecutiveDays >= 7) {
            rewardAmount = DAILY_LOGIN_DIAMOND_AMOUNT;
            rewardCurrency = CurrencyType.DIAMONDS.name();
        } else if (consecutiveDays >= 3) {
            rewardAmount = DAILY_LOGIN_GOLD_AMOUNT.multiply(new BigDecimal("2"));
        }

        return DailyLoginRewardDto.builder()
            .canClaimToday(canClaimToday)
            .rewardAmountToday(rewardAmount)
            .rewardCurrencyToday(rewardCurrency)
            .consecutiveDays(consecutiveDays)
            .lastClaimDate(lastReward != null ? lastReward.getClaimDate() : null)
            .message(canClaimToday ? "Claim your daily login reward!" : "You have already claimed today's reward.")
            .build();
    }


    @Override
    @Transactional
    public CurrencyOperationResponseDto completeSpecificTask(Long userId, String taskIdentifier, BigDecimal rewardAmount, CurrencyType currencyType) {
        // Implement logic to ensure task is only rewarded once, or according to task rules
        // For simplicity, this example just credits the reward.
        // A more complex system would check a TaskCompletion entity for user+taskIdentifier

        AddCurrencyRequestDto addRequest = AddCurrencyRequestDto.builder()
            .currencyType(currencyType.name())
            .amount(rewardAmount)
            .sourceType(TransactionSource.TASK_REWARD.name())
            .sourceDescription("Reward for completing task: " + taskIdentifier)
            .build();

        CurrencyOperationResponseDto walletResult = walletService.addCurrency(userId, addRequest);
        walletResult.setMessage("Task '" + taskIdentifier + "' completed. Received " + rewardAmount.stripTrailingZeros().toPlainString() + " " + currencyType.name());
        return walletResult;
    }

    private TaskRewardDto convertToTaskRewardDto(WalletTransaction transaction) {
        String formattedDate = transaction.getTransactionDate().format(DATE_FORMATTER);
        String timeAgo = calculateTimeAgo(transaction.getTransactionDate());
        String rewardAmountStr = (transaction.getTransactionType() == TransactionType.CREDIT ? "+" : "-") + 
                                 transaction.getAmount().stripTrailingZeros().toPlainString();

        return TaskRewardDto.builder()
            // Using transaction ID as rewardId for simplicity here
            .rewardId(transaction.getTransactionId()) 
            .taskType("Task rewards") // As seen in screenshots
            .rewardAmount(rewardAmountStr)
            .currencyType(transaction.getCurrencyType().name())
            .currencyIcon(transaction.getCurrencyType().getIcon())
            .rewardDate(transaction.getTransactionDate())
            .formattedDate(formattedDate)
            .timeAgo(timeAgo)
            .displayText("Task rewards " + rewardAmountStr + transaction.getCurrencyType().getIcon())
            .displayAmount(rewardAmountStr)
            .build();
    }

    private String calculateTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        if (minutes < 60) return minutes + " minutes ago";
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) return hours + " hours ago";
        long days = ChronoUnit.DAYS.between(dateTime, now);
        return days + " days ago";
    }
}
