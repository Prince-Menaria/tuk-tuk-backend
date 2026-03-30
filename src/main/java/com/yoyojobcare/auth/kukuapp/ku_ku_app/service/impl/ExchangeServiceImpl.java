package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.ExchangeService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.WalletService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.DeductCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.ExchangeRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.ExchangeRatesDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.ExchangeResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletBalanceResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeServiceImpl implements ExchangeService {

    private final WalletService walletService;

    // Fixed exchange rate for now, can be configured in properties or an entity
    private static final BigDecimal DIAMOND_TO_GOLD_RATE = BigDecimal.TEN; // 1 Diamond = 10 Gold (Screenshot 5)

    @Override
    @Transactional
    public ExchangeResponseDto exchangeDiamondsToGold(Long userId, ExchangeRequestDto request) {
        BigDecimal diamondsToExchange = request.getDiamondAmount();
        BigDecimal goldToReceive = diamondsToExchange.multiply(DIAMOND_TO_GOLD_RATE);

        // 1. Deduct Diamonds
        DeductCurrencyRequestDto deductDiamondsRequest = DeductCurrencyRequestDto.builder()
            .currencyType(CurrencyType.DIAMONDS.name())
            .amount(diamondsToExchange)
            .sourceType(TransactionSource.EXCHANGE_DIAMONDS.name())
            .sourceDescription("Exchanged diamonds for gold")
            .build();
        
        CurrencyOperationResponseDto diamondDeductionResult = walletService.deductCurrency(userId, deductDiamondsRequest);
        if (!diamondDeductionResult.getSuccess()) {
            throw new RuntimeException("Failed to deduct diamonds: " + diamondDeductionResult.getMessage());
        }

        // 2. Add Gold
        AddCurrencyRequestDto addGoldRequest = AddCurrencyRequestDto.builder()
            .currencyType(CurrencyType.GOLD.name())
            .amount(goldToReceive)
            .sourceType(TransactionSource.EXCHANGE_GOLD.name())
            .sourceDescription("Received gold from diamond exchange")
            .build();

        CurrencyOperationResponseDto goldAdditionResult = walletService.addCurrency(userId, addGoldRequest);
        if (!goldAdditionResult.getSuccess()) {
            // Rollback might be needed if addCurrency failed but deductCurrency succeeded
            // This is handled by @Transactional if it throws an exception.
            throw new RuntimeException("Failed to add gold after diamond deduction: " + goldAdditionResult.getMessage());
        }

        // 3. Get updated wallet balance
        WalletBalanceResponseDto updatedWallet = walletService.getUserWallet(userId);

        log.info("User {} exchanged {} diamonds for {} gold.", userId, diamondsToExchange, goldToReceive);

        return ExchangeResponseDto.builder()
            .success(true)
            .message(diamondsToExchange + " diamonds exchanged for " + goldToReceive + " gold.")
            .diamondsExchanged(diamondsToExchange)
            .goldReceived(goldToReceive)
            .exchangeRate(DIAMOND_TO_GOLD_RATE)
            .remainingDiamonds(updatedWallet.getDiamonds())
            .newGoldBalance(updatedWallet.getGold())
            .transactionId(diamondDeductionResult.getTransactionId()) // Reference to diamond debit
            .creditTransactionId(goldAdditionResult.getTransactionId()) // Reference to gold credit
            .referenceId(diamondDeductionResult.getReferenceId())
            .build();
    }

    @Override
    public ExchangeRatesDto getExchangeRates() {
        return ExchangeRatesDto.builder()
            .diamondToGoldRate(DIAMOND_TO_GOLD_RATE)
            .rateDescription("1 Diamond = " + DIAMOND_TO_GOLD_RATE.toPlainString() + " Gold")
            .exchangeEnabled(true) // Assume always enabled
            .build();
    }
}


