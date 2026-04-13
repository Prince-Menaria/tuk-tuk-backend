package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.List;

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

public interface WalletService {

    WalletBalanceResponseDto getUserWallet(Long userId);

    CurrencyOperationResponseDto addCurrency(Long userId, AddCurrencyRequestDto request);

    CurrencyOperationResponseDto deductCurrency(Long userId, AddCurrencyRequestDto request);

    List<TransactionResponseDto> getTransactionHistory(Long userId, int page, int size);

    ExchangeResponseDto exchangeDiamondsToGold(Long userId, ExchangeRequestDto request);

    List<RechargePackageDto> getRechargePackages();

    PurchaseResponseDto purchaseDiamonds(Long userId, PurchaseRequestDto request);

    AddRechargePackageResponseDto addRechargePackage(AddRechargePackageRequestDto requestDto);

}
