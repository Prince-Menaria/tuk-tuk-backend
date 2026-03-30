package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserWallet;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.WalletTransaction;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.WalletStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserWalletRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.WalletTransactionRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.WalletService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.DeductCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.GetTransactionsRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.TransactionResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletBalanceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletTransactionHistoryResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final UserWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository; // Assuming UserRepository exists

    @Override
    @Transactional(readOnly = true)
    public WalletBalanceResponseDto getUserWallet(Long userId) {
        UserWallet wallet = walletRepository.findByUserUserId(userId)
            .orElseGet(() -> { // Create if not found
                log.warn("Wallet not found for user {}, creating new one.", userId);
                return createWalletForUserEntity(userId);
            });

        return WalletBalanceResponseDto.builder()
            .userId(userId)
            .userName(wallet.getUser().getFullName())
            .diamonds(wallet.getDiamonds())
            .gold(wallet.getGold())
            .totalDiamondsEarned(wallet.getTotalDiamondsEarned())
            .totalGoldEarned(wallet.getTotalGoldEarned())
            .walletStatus(wallet.getWalletStatus().name())
            .lastUpdated(wallet.getLastUpdated())
            .build();
    }

    @Override
    @Transactional
    public WalletBalanceResponseDto createWalletForUser(Long userId) {
        if (walletRepository.existsByUserUserId(userId)) {
            throw new RuntimeException("Wallet already exists for user: " + userId);
        }
        UserWallet newWallet = createWalletForUserEntity(userId);
        return getUserWallet(userId);
    }

    private UserWallet createWalletForUserEntity(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        UserWallet wallet = UserWallet.builder()
            .user(user)
            .diamonds(BigDecimal.ZERO)
            .gold(BigDecimal.ZERO)
            .totalDiamondsEarned(BigDecimal.ZERO)
            .totalGoldEarned(BigDecimal.ZERO)
            .walletStatus(WalletStatus.ACTIVE)
            .lastUpdated(LocalDateTime.now())
            .build();

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public CurrencyOperationResponseDto addCurrency(Long userId, AddCurrencyRequestDto request) {
        UserWallet wallet = walletRepository.findByUserUserId(userId)
            .orElseGet(() -> createWalletForUserEntity(userId));

        CurrencyType currencyType = CurrencyType.valueOf(request.getCurrencyType().toUpperCase());
        BigDecimal previousBalance = (currencyType == CurrencyType.DIAMONDS) ? wallet.getDiamonds() : wallet.getGold();

        // Use optimized repository method for direct update
        int updatedRows;
        if (currencyType == CurrencyType.DIAMONDS) {
            updatedRows = walletRepository.addDiamondsAmount(userId, request.getAmount());
        } else {
            updatedRows = walletRepository.addGoldAmount(userId, request.getAmount());
        }

        if (updatedRows == 0) {
            throw new RuntimeException("Failed to update wallet balance. User or wallet not found.");
        }
        
        // Re-fetch wallet to get updated balance
        wallet = walletRepository.findByUserUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found after update."));

        BigDecimal newBalance = (currencyType == CurrencyType.DIAMONDS) ? wallet.getDiamonds() : wallet.getGold();

        // Create transaction record
        WalletTransaction transaction = WalletTransaction.builder()
            .user(wallet.getUser())
            .currencyType(currencyType)
            .transactionType(TransactionType.CREDIT)
            .amount(request.getAmount())
            .balanceBefore(previousBalance)
            .balanceAfter(newBalance)
            .sourceType(TransactionSource.valueOf(request.getSourceType().toUpperCase()))
            .sourceDescription(request.getSourceDescription())
            .transactionDate(LocalDateTime.now())
            .status(TransactionStatus.COMPLETED)
            .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);

        log.info("Added {} {} to user {}", request.getAmount(), currencyType, userId);

        return CurrencyOperationResponseDto.builder()
            .success(true)
            .message("Currency added successfully")
            .transactionId(savedTransaction.getTransactionId())
            .referenceId(savedTransaction.getReferenceId())
            .transactionDate(savedTransaction.getTransactionDate())
            .currencyType(currencyType.name())
            .amount(request.getAmount())
            .formattedAmount("+" + request.getAmount() + " " + currencyType.getIcon())
            .previousBalance(previousBalance)
            .newBalance(newBalance)
            .sourceType(request.getSourceType())
            .sourceDescription(request.getSourceDescription())
            .build();
    }

    @Override
    @Transactional
    public CurrencyOperationResponseDto deductCurrency(Long userId, DeductCurrencyRequestDto request) {
        UserWallet wallet = walletRepository.findByUserUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));

        CurrencyType currencyType = CurrencyType.valueOf(request.getCurrencyType().toUpperCase());
        BigDecimal previousBalance = (currencyType == CurrencyType.DIAMONDS) ? wallet.getDiamonds() : wallet.getGold();

        int updatedRows;
        if (currencyType == CurrencyType.DIAMONDS) {
            updatedRows = walletRepository.deductDiamondsAmount(userId, request.getAmount());
        } else {
            updatedRows = walletRepository.deductGoldAmount(userId, request.getAmount());
        }

        if (updatedRows == 0) {
            throw new RuntimeException("Insufficient " + currencyType.name().toLowerCase() + " balance or wallet not found.");
        }

        // Re-fetch wallet to get updated balance
        wallet = walletRepository.findByUserUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found after update."));

        BigDecimal newBalance = (currencyType == CurrencyType.DIAMONDS) ? wallet.getDiamonds() : wallet.getGold();

        // Create transaction record
        WalletTransaction transaction = WalletTransaction.builder()
            .user(wallet.getUser())
            .currencyType(currencyType)
            .transactionType(TransactionType.DEBIT)
            .amount(request.getAmount())
            .balanceBefore(previousBalance)
            .balanceAfter(newBalance)
            .sourceType(TransactionSource.valueOf(request.getSourceType().toUpperCase()))
            .sourceDescription(request.getSourceDescription())
            .transactionDate(LocalDateTime.now())
            .status(TransactionStatus.COMPLETED)
            .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);

        log.info("Deducted {} {} from user {}", request.getAmount(), currencyType, userId);

        return CurrencyOperationResponseDto.builder()
            .success(true)
            .message("Currency deducted successfully")
            .transactionId(savedTransaction.getTransactionId())
            .referenceId(savedTransaction.getReferenceId())
            .transactionDate(savedTransaction.getTransactionDate())
            .currencyType(currencyType.name())
            .amount(request.getAmount())
            .formattedAmount("-" + request.getAmount() + " " + currencyType.getIcon())
            .previousBalance(previousBalance)
            .newBalance(newBalance)
            .sourceType(request.getSourceType())
            .sourceDescription(request.getSourceDescription())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WalletTransactionHistoryResponseDto getTransactionHistory(Long userId, GetTransactionsRequestDto request) {
        Sort sort = Sort.by(
            request.getSortDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
            request.getSortBy()
        );

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<WalletTransaction> transactionPage;
        if (request.getCurrencyType() != null && !request.getCurrencyType().equalsIgnoreCase("ALL")) {
            CurrencyType currencyType = CurrencyType.valueOf(request.getCurrencyType().toUpperCase());
            transactionPage = transactionRepository.findByUserIdAndCurrencyTypeOrderByTransactionDateDesc(userId, currencyType, pageRequest);
        } else {
            transactionPage = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId, pageRequest);
        }

        List<TransactionResponseDto> transactions = transactionPage.getContent().stream()
            .map(this::convertToTransactionDto)
            .collect(Collectors.toList());

        return WalletTransactionHistoryResponseDto.builder()
            .currentPage(request.getPage())
            .pageSize(request.getSize())
            .totalTransactions(transactionPage.getTotalElements())
            .totalPages(transactionPage.getTotalPages())
            .hasNext(transactionPage.hasNext())
            .hasPrevious(transactionPage.hasPrevious())
            .transactions(transactions)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientDiamonds(Long userId, BigDecimal amount) {
        return walletRepository.findByUserUserId(userId)
            .map(wallet -> wallet.getDiamonds().compareTo(amount) >= 0)
            .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientGold(Long userId, BigDecimal amount) {
        return walletRepository.findByUserUserId(userId)
            .map(wallet -> wallet.getGold().compareTo(amount) >= 0)
            .orElse(false);
    }

    private TransactionResponseDto convertToTransactionDto(WalletTransaction transaction) {
        String sign = transaction.getTransactionType() == TransactionType.CREDIT ? "+" : "-";
        String formattedAmount = sign + transaction.getAmount().stripTrailingZeros().toPlainString() + " " + transaction.getCurrencyType().getIcon();

        return TransactionResponseDto.builder()
            .transactionId(transaction.getTransactionId())
            .referenceId(transaction.getReferenceId())
            .currencyType(transaction.getCurrencyType().name())
            .currencyIcon(transaction.getCurrencyType().getIcon())
            .transactionType(transaction.getTransactionType().name())
            .amount(transaction.getAmount())
            .balanceBefore(transaction.getBalanceBefore())
            .balanceAfter(transaction.getBalanceAfter())
            .sourceType(transaction.getSourceType().name())
            .sourceDescription(transaction.getSourceDescription())
            .transactionDate(transaction.getTransactionDate())
            .status(transaction.getStatus().name())
            .formattedAmount(formattedAmount)
            .build();
    }
}
