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

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RechargePackage;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RechargePackage.RechargePackageBuilder;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserWallet;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.WalletTransaction;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.WalletStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RechargePackageRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserWalletRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.WalletTransactionRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.WalletService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddRechargePackageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.ExchangeRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.PurchaseRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.AddRechargePackageResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.ExchangeResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.PurchaseResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.RechargePackageDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.TransactionResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletBalanceResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final UserWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final RechargePackageRepository packageRepository;
    private final UserRepository userRepository;

    private static final BigDecimal DIAMOND_TO_GOLD_RATE = BigDecimal.TEN; // 1 Diamond = 10 Gold

    @Override
    @Transactional(readOnly = true)
    public WalletBalanceResponseDto getUserWallet(Long userId) {
        log.info("Request for userId : " + userId);
        try {
            UserWallet wallet = walletRepository.findByUserUserId(userId)
                    .orElseGet(() -> createWallet(userId));

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

        } catch (Exception e) {
            log.error("get User Wallet Exception : " + e);
            return null;
        }

    }

    @Override
    @Transactional
    public CurrencyOperationResponseDto addCurrency(Long userId, AddCurrencyRequestDto request) {
        try {
            UserWallet wallet = getOrCreateWallet(userId);

            CurrencyType currencyType = CurrencyType.valueOf(request.getCurrencyType().toUpperCase());
            BigDecimal previousBalance = (currencyType == CurrencyType.DIAMONDS) ? wallet.getDiamonds()
                    : wallet.getGold();

            if (currencyType == CurrencyType.DIAMONDS) {
                wallet.addDiamonds(request.getAmount());
            } else {
                wallet.addGold(request.getAmount());
            }

            UserWallet savedWallet = walletRepository.save(wallet);
            BigDecimal newBalance = (currencyType == CurrencyType.DIAMONDS) ? savedWallet.getDiamonds()
                    : savedWallet.getGold();

            WalletTransaction transaction = createTransaction(
                    wallet.getUser(), currencyType, TransactionType.CREDIT,
                    request.getAmount(), previousBalance, newBalance,
                    TransactionSource.valueOf(request.getSourceType().toUpperCase()),
                    request.getSourceDescription());

            log.info("Added {} {} to user {}", request.getAmount(), currencyType, userId);

            return CurrencyOperationResponseDto.builder()
                    .success(true)
                    .message("Currency added successfully")
                    .transactionId(transaction.getTransactionId())
                    .referenceId(transaction.getReferenceId())
                    .transactionDate(transaction.getTransactionDate())
                    .currencyType(currencyType.name())
                    .amount(request.getAmount())
                    .formattedAmount("+" + request.getAmount() + " " + currencyType.getIcon())
                    .previousBalance(previousBalance)
                    .newBalance(newBalance)
                    .sourceType(request.getSourceType())
                    .build();

        } catch (Exception e) {
            log.error("add Currency Wallet Exception : " + e);
            throw e;
        }

    }

    @Override
    @Transactional
    public CurrencyOperationResponseDto deductCurrency(Long userId, AddCurrencyRequestDto request) {
        try {
            UserWallet wallet = getOrCreateWallet(userId);

            CurrencyType currencyType = CurrencyType.valueOf(request.getCurrencyType().toUpperCase());
            BigDecimal previousBalance = (currencyType == CurrencyType.DIAMONDS) ? wallet.getDiamonds()
                    : wallet.getGold();

            boolean success;
            if (currencyType == CurrencyType.DIAMONDS) {
                success = wallet.deductDiamonds(request.getAmount());
            } else {
                success = wallet.deductGold(request.getAmount());
            }

            if (!success) {
                throw new RuntimeException("Insufficient " + currencyType.name().toLowerCase() + " balance");
            }

            UserWallet savedWallet = walletRepository.save(wallet);
            BigDecimal newBalance = (currencyType == CurrencyType.DIAMONDS) ? savedWallet.getDiamonds()
                    : savedWallet.getGold();

            WalletTransaction transaction = createTransaction(
                    wallet.getUser(), currencyType, TransactionType.DEBIT,
                    request.getAmount(), previousBalance, newBalance,
                    TransactionSource.valueOf(request.getSourceType().toUpperCase()),
                    request.getSourceDescription());

            return CurrencyOperationResponseDto.builder()
                    .success(true)
                    .message("Currency deducted successfully")
                    .transactionId(transaction.getTransactionId())
                    .referenceId(transaction.getReferenceId())
                    .transactionDate(transaction.getTransactionDate())
                    .currencyType(currencyType.name())
                    .amount(request.getAmount())
                    .formattedAmount("-" + request.getAmount() + " " + currencyType.getIcon())
                    .previousBalance(previousBalance)
                    .newBalance(newBalance)
                    .sourceType(request.getSourceType())
                    .build();
        } catch (Exception e) {
            log.error("deduct Currency Wallet Exception : " + e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionHistory(Long userId, int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionDate").descending());
            Page<WalletTransaction> transactions = transactionRepository
                    .findByUserUserIdOrderByTransactionDateDesc(userId, pageRequest);

            return transactions.getContent().stream()
                    .map(this::convertToTransactionDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("get Transaction History Wallet Exception : " + e);
            throw e;
        }

    }

    @Override
    @Transactional
    public ExchangeResponseDto exchangeDiamondsToGold(Long userId, ExchangeRequestDto request) {

        try {
            BigDecimal goldToReceive = request.getDiamondAmount().multiply(DIAMOND_TO_GOLD_RATE);

            // Deduct diamonds
            AddCurrencyRequestDto deductRequest = AddCurrencyRequestDto.builder()
                    .currencyType(CurrencyType.DIAMONDS.name())
                    .amount(request.getDiamondAmount())
                    .sourceType(TransactionSource.EXCHANGE_DIAMONDS.name())
                    .sourceDescription("Exchanged diamonds for gold")
                    .build();

            CurrencyOperationResponseDto deductResult = deductCurrency(userId, deductRequest);

            // Add gold
            AddCurrencyRequestDto addRequest = AddCurrencyRequestDto.builder()
                    .currencyType(CurrencyType.GOLD.name())
                    .amount(goldToReceive)
                    .sourceType(TransactionSource.EXCHANGE_GOLD.name())
                    .sourceDescription("Received gold from diamond exchange")
                    .build();

            CurrencyOperationResponseDto addResult = addCurrency(userId, addRequest);

            WalletBalanceResponseDto updatedWallet = getUserWallet(userId);

            return ExchangeResponseDto.builder()
                    .success(true)
                    .message(request.getDiamondAmount() + " diamonds exchanged for " + goldToReceive + " gold")
                    .diamondsExchanged(request.getDiamondAmount())
                    .goldReceived(goldToReceive)
                    .exchangeRate(DIAMOND_TO_GOLD_RATE)
                    .remainingDiamonds(updatedWallet.getDiamonds())
                    .newGoldBalance(updatedWallet.getGold())
                    .transactionId(deductResult.getTransactionId())
                    .referenceId(deductResult.getReferenceId())
                    .build();

        } catch (Exception e) {
            log.error("exchange Diamonds To Gold Wallet Exception : " + e);
            throw e;

        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<RechargePackageDto> getRechargePackages() {
        try {
            return packageRepository.findByIsActiveTrueOrderByOrderIndexAsc().stream()
                    .map(this::convertToPackageDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("exchange Diamonds To Gold Wallet Exception : " + e);
            throw e;
        }

    }

    @Override
    @Transactional
    public PurchaseResponseDto purchaseDiamonds(Long userId, PurchaseRequestDto request) {
        try {
            RechargePackage pkg = packageRepository.findByPackageIdAndIsActiveTrue(request.getPackageId())
                    .orElseThrow(() -> new RuntimeException("Package not found"));

            // Simulate payment (replace with actual payment gateway)
            String paymentId = "PAY_" + System.currentTimeMillis();

            // Add diamonds to wallet
            AddCurrencyRequestDto addRequest = AddCurrencyRequestDto.builder()
                    .currencyType(CurrencyType.DIAMONDS.name())
                    .amount(pkg.getDiamondsAmount())
                    .sourceType(TransactionSource.PURCHASE.name())
                    .sourceDescription("Purchased " + pkg.getPackageName())
                    .build();

            CurrencyOperationResponseDto result = addCurrency(userId, addRequest);
            WalletBalanceResponseDto updatedWallet = getUserWallet(userId);

            return PurchaseResponseDto.builder()
                    .success(true)
                    .message("Diamonds purchased successfully!")
                    .packageId(pkg.getPackageId())
                    .diamondsPurchased(pkg.getDiamondsAmount())
                    .amountPaid(pkg.getPrice())
                    .currency(pkg.getCurrency())
                    .paymentId(paymentId)
                    .paymentStatus("COMPLETED")
                    .newDiamondBalance(updatedWallet.getDiamonds())
                    .transactionId(result.getTransactionId())
                    .referenceId(result.getReferenceId())
                    .build();

        } catch (Exception e) {
            log.error("exchange Diamonds To Gold Wallet Exception : " + e);
            throw e;
        }

    }

    @Override
    public AddRechargePackageResponseDto addRechargePackage(AddRechargePackageRequestDto requestDto) {
        log.info("Add Recharge Package Request : " + requestDto);
        try {
            RechargePackage savePackage = RechargePackage.builder()
                    .packageName(requestDto.getPackageName())
                    .diamondsAmount(requestDto.getDiamondsAmount())
                    .price(requestDto.getPrice())
                    .build();

            savePackage = this.packageRepository.save(savePackage);
            log.info("Package save successful....");

            return AddRechargePackageResponseDto.builder()
                    .packageId(savePackage.getPackageId())
                    .build();

        } catch (Exception e) {
            log.error("add Recharge Package Wallet Exception : " + e);
            throw e;
        }
    }

    // Helper methods
    private UserWallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserUserId(userId)
                .orElseGet(() -> createWallet(userId));
    }

    private UserWallet createWallet(Long userId) {
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

    private WalletTransaction createTransaction(User user, CurrencyType currencyType,
            TransactionType transactionType, BigDecimal amount,
            BigDecimal balanceBefore, BigDecimal balanceAfter,
            TransactionSource sourceType, String sourceDescription) {

        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .currencyType(currencyType)
                .transactionType(transactionType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .sourceType(sourceType)
                .sourceDescription(sourceDescription)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .build();

        return transactionRepository.save(transaction);
    }

    private TransactionResponseDto convertToTransactionDto(WalletTransaction transaction) {
        String sign = transaction.getTransactionType() == TransactionType.CREDIT ? "+" : "-";
        String formattedAmount = sign + transaction.getAmount().stripTrailingZeros().toPlainString() +
                " " + transaction.getCurrencyType().getIcon();

        return TransactionResponseDto.builder()
                .transactionId(transaction.getTransactionId())
                .referenceId(transaction.getReferenceId())
                .currencyType(transaction.getCurrencyType().name())
                .currencyIcon(transaction.getCurrencyType().getIcon())
                .transactionType(transaction.getTransactionType().name())
                .amount(transaction.getAmount())
                .sourceType(transaction.getSourceType().name())
                .sourceDescription(transaction.getSourceDescription())
                .transactionDate(transaction.getTransactionDate())
                .status(transaction.getStatus().name())
                .formattedAmount(formattedAmount)
                .build();
    }

    private RechargePackageDto convertToPackageDto(RechargePackage pkg) {
        String displayDiamonds = formatDiamonds(pkg.getDiamondsAmount());
        String displayPrice = pkg.getCurrency() + " " + pkg.getPrice().stripTrailingZeros().toPlainString();

        return RechargePackageDto.builder()
                .packageId(pkg.getPackageId())
                .packageName(pkg.getPackageName())
                .diamonds(pkg.getDiamondsAmount())
                .price(pkg.getPrice())
                .currency(pkg.getCurrency())
                .isPopular(pkg.getIsPopular())
                .hasDiscount(pkg.getHasDiscount())
                .badgeText(pkg.getBadgeText())
                .displayDiamonds(displayDiamonds)
                .displayPrice(displayPrice)
                .build();
    }

    private String formatDiamonds(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("1000")) >= 0) {
            return amount.divide(new BigDecimal("1000")).stripTrailingZeros().toPlainString() + "K";
        }
        return amount.stripTrailingZeros().toPlainString();
    }

}
