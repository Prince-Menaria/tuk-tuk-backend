package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.math.BigDecimal;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.DeductCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.GetTransactionsRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletBalanceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletTransactionHistoryResponseDto;

public interface WalletService {

    WalletBalanceResponseDto getUserWallet(Long userId);

    WalletBalanceResponseDto createWalletForUser(Long userId); // Renamed for clarity

    CurrencyOperationResponseDto addCurrency(Long userId, AddCurrencyRequestDto request);

    CurrencyOperationResponseDto deductCurrency(Long userId, DeductCurrencyRequestDto request);

    WalletTransactionHistoryResponseDto getTransactionHistory(Long userId, GetTransactionsRequestDto request);

    boolean hasSufficientDiamonds(Long userId, BigDecimal amount);

    boolean hasSufficientGold(Long userId, BigDecimal amount);

}
