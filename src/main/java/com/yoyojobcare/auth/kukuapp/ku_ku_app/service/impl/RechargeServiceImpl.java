package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RechargePackage;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RechargePackageRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.RechargeService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.WalletService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.PurchaseRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.RechargePackageDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.PurchaseResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletBalanceResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeServiceImpl implements RechargeService {

    private final RechargePackageRepository rechargePackageRepository;
    private final WalletService walletService;

    // --- Initial data setup (Can be done via data.sql or admin panel) ---
    // This is just a sample for demonstration based on Screenshot 1
    // In a real app, these would come from DB.
    // Ensure these exist in your database before running.
    // If not, use an @PostConstruct method or data.sql to populate.
    /*
    @PostConstruct
    public void initRechargePackages() {
        if (rechargePackageRepository.count() == 0) {
            log.info("Populating initial recharge packages...");
            rechargePackageRepository.saveAll(Arrays.asList(
                RechargePackage.builder().packageId(1L).packageName("Basic").diamondsAmount(new BigDecimal("3600")).price(new BigDecimal("56")).currency("INR").badgeText("+0K").orderIndex(1).build(),
                RechargePackage.builder().packageId(2L).packageName("Popular").diamondsAmount(new BigDecimal("36000")).price(new BigDecimal("563")).currency("INR").hasDiscount(true).originalPrice(new BigDecimal("650")).discountPercentage(13).badgeText("+24.0K").isPopular(true).orderIndex(2).build(),
                RechargePackage.builder().packageId(3L).packageName("Value").diamondsAmount(new BigDecimal("60000")).price(new BigDecimal("939")).currency("INR").hasDiscount(true).originalPrice(new BigDecimal("1000")).discountPercentage(6).badgeText("+40.0K").orderIndex(3).build(),
                RechargePackage.builder().packageId(4L).packageName("Super").diamondsAmount(new BigDecimal("240000")).price(new BigDecimal("3750")).currency("INR").hasDiscount(true).originalPrice(new BigDecimal("4000")).discountPercentage(6).badgeText("+160.0K").orderIndex(4).build(),
                RechargePackage.builder().packageId(5L).packageName("Mega").diamondsAmount(new BigDecimal("540000")).price(new BigDecimal("8450")).currency("INR").hasDiscount(true).originalPrice(new BigDecimal("9000")).discountPercentage(6).badgeText("+360.0K").orderIndex(5).build(),
                RechargePackage.builder().packageId(6L).packageName("Ultimate").diamondsAmount(new BigDecimal("1080000")).price(new BigDecimal("16900")).currency("INR").hasDiscount(true).originalPrice(new BigDecimal("18000")).discountPercentage(6).badgeText("+720.0K").orderIndex(6).build()
            ));
            log.info("Initial recharge packages populated.");
        }
    }
    */

    @Override
    @Transactional(readOnly = true)
    public List<RechargePackageDto> getRechargePackages() {
        return rechargePackageRepository.findByIsActiveTrueOrderByOrderIndexAsc().stream()
            .map(this::convertToRechargePackageDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseResponseDto purchaseDiamonds(Long userId, PurchaseRequestDto request) {
        RechargePackage pkg = rechargePackageRepository.findByPackageIdAndIsActiveTrue(request.getPackageId())
            .orElseThrow(() -> new RuntimeException("Recharge package not found or inactive: " + request.getPackageId()));

        // --- Simulate Payment Gateway Interaction ---
        // In a real application, this would involve calling an external payment gateway (e.g., Stripe, Razorpay)
        // This is a placeholder for successful payment.
        boolean paymentSuccessful = simulatePayment(pkg.getPrice(), request.getPaymentMethod());
        String paymentGatewayTxnId = "PG_" + System.currentTimeMillis();

        if (!paymentSuccessful) {
            throw new RuntimeException("Payment failed for package " + pkg.getPackageName());
        }

        // Add diamonds to user's wallet
        AddCurrencyRequestDto addDiamondsRequest = AddCurrencyRequestDto.builder()
            .currencyType(CurrencyType.DIAMONDS.name())
            .amount(pkg.getDiamondsAmount())
            .sourceType(TransactionSource.PURCHASE.name())
            .sourceDescription("Purchased " + pkg.getDiamondsAmount().stripTrailingZeros().toPlainString() + " diamonds via " + pkg.getPackageName())
            .build();
        
        CurrencyOperationResponseDto addResult = walletService.addCurrency(userId, addDiamondsRequest);

        if (!addResult.getSuccess()) {
            // Payment succeeded but adding diamonds failed - this is a critical error.
            // You might need to implement a compensation transaction or alert system.
            log.error("CRITICAL: Payment for user {} package {} succeeded, but adding diamonds failed.", userId, request.getPackageId());
            throw new RuntimeException("Purchase completed, but failed to credit diamonds. Please contact support.");
        }

        log.info("User {} successfully purchased {} diamonds for INR {}", userId, pkg.getDiamondsAmount(), pkg.getPrice());
        
        // Get updated wallet balance
        WalletBalanceResponseDto updatedWallet = walletService.getUserWallet(userId);

        return PurchaseResponseDto.builder()
            .success(true)
            .message("Diamonds purchased successfully!")
            .packageId(pkg.getPackageId())
            .diamondsPurchased(pkg.getDiamondsAmount())
            .amountPaid(pkg.getPrice())
            .currency(pkg.getCurrency())
            .paymentId(paymentGatewayTxnId)
            .paymentStatus("COMPLETED")
            .paymentMethod(request.getPaymentMethod())
            .newDiamondBalance(updatedWallet.getDiamonds())
            .transactionId(addResult.getTransactionId())
            .referenceId(addResult.getReferenceId())
            .build();
    }

    private RechargePackageDto convertToRechargePackageDto(RechargePackage pkg) {
        String displayDiamonds;
        if (pkg.getDiamondsAmount().compareTo(new BigDecimal("1000")) >= 0) {
            displayDiamonds = pkg.getDiamondsAmount().divide(new BigDecimal("1000")).stripTrailingZeros().toPlainString() + "K";
        } else {
            displayDiamonds = pkg.getDiamondsAmount().stripTrailingZeros().toPlainString();
        }

        String badgeText = pkg.getBadgeText();
        if (pkg.getHasDiscount() && pkg.getOriginalPrice() != null) {
            BigDecimal bonusDiamonds = pkg.getDiamondsAmount().subtract(pkg.getOriginalPrice().multiply(BigDecimal.valueOf(100))); // Assuming a base rate to calculate bonus
            // This logic needs to be more robust based on how bonus diamonds are calculated in your app
            // For now, using the badgeText from the entity directly if it exists.
        }
        
        return RechargePackageDto.builder()
            .packageId(pkg.getPackageId())
            .packageName(pkg.getPackageName())
            .diamonds(pkg.getDiamondsAmount())
            .price(pkg.getPrice())
            .currency(pkg.getCurrency())
            .isPopular(pkg.getIsPopular())
            .hasDiscount(pkg.getHasDiscount())
            .originalPrice(pkg.getOriginalPrice())
            .discountPercentage(pkg.getDiscountPercentage())
            .badgeText(pkg.getBadgeText()) // Use badgeText from entity directly
            .displayDiamonds(displayDiamonds)
            .displayPrice(pkg.getCurrency() + " " + pkg.getPrice().stripTrailingZeros().toPlainString())
            .build();
    }

    // Placeholder for actual payment gateway call
    private boolean simulatePayment(BigDecimal amount, String paymentMethod) {
        log.info("Simulating payment of {} via {}", amount, paymentMethod);
        // In a real app, this would be an API call to a payment provider
        return true; // Always successful for demo purposes
    }
}
