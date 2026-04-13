package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    // private final CurrentUserService currentUserService;

    @GetMapping("/balance")
    public ResponseEntity<MobileResponse<WalletBalanceResponseDto>> getBalance(
            // @AuthenticationPrincipal OAuth2User currentUser
            @RequestParam Long userId) {
        try {
            // Long userId = currentUserService.getCurrentUserId(currentUser);
            WalletBalanceResponseDto response = walletService.getUserWallet(userId);
            return ResponseEntity.ok(MobileResponse.<WalletBalanceResponseDto>builder()
                    .status(true).message("Balance retrieved successfully").data(response).build());
        } catch (Exception e) {
            log.error("Error getting balance: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MobileResponse.<WalletBalanceResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<MobileResponse<List<TransactionResponseDto>>> getTransactions(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam Long userId) {
        try {
            // Long userId = currentUserService.getCurrentUserId(currentUser);
            List<TransactionResponseDto> response = walletService.getTransactionHistory(userId, page, size);
            return ResponseEntity.ok(MobileResponse.<List<TransactionResponseDto>>builder()
                    .status(true).message("Transactions retrieved successfully").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<List<TransactionResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/exchange")
    public ResponseEntity<MobileResponse<ExchangeResponseDto>> exchangeDiamondsToGold(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestBody ExchangeRequestDto request,
            @RequestParam Long userId) {
        try {
            // Long userId = currentUserService.getCurrentUserId(currentUser);
            ExchangeResponseDto response = walletService.exchangeDiamondsToGold(userId, request);
            return ResponseEntity.ok(MobileResponse.<ExchangeResponseDto>builder()
                    .status(response.getSuccess()).message(response.getMessage()).data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<ExchangeResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    @GetMapping("/recharge/packages")
    public ResponseEntity<MobileResponse<List<RechargePackageDto>>> getRechargePackages() {
        try {
            List<RechargePackageDto> response = walletService.getRechargePackages();
            return ResponseEntity.ok(MobileResponse.<List<RechargePackageDto>>builder()
                    .status(true).message("Packages retrieved successfully").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<List<RechargePackageDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/recharge/purchase")
    public ResponseEntity<MobileResponse<PurchaseResponseDto>> purchaseDiamonds(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestBody PurchaseRequestDto request,
            @RequestParam Long userId) {
        try {
            // Long userId = currentUserService.getCurrentUserId(currentUser);
            PurchaseResponseDto response = walletService.purchaseDiamonds(userId, request);
            return ResponseEntity.ok(MobileResponse.<PurchaseResponseDto>builder()
                    .status(response.getSuccess()).message(response.getMessage()).data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<PurchaseResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/add") // Admin endpoint
    public ResponseEntity<MobileResponse<CurrencyOperationResponseDto>> addCurrency(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long targetUserId,
            @RequestBody AddCurrencyRequestDto request) {
        try {
            CurrencyOperationResponseDto response = walletService.addCurrency(targetUserId, request);
            return ResponseEntity.ok(MobileResponse.<CurrencyOperationResponseDto>builder()
                    .status(response.getSuccess()).message(response.getMessage()).data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<CurrencyOperationResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/add-package") // Admin endpoint
    public ResponseEntity<MobileResponse<AddRechargePackageResponseDto>> addPackage(
            // @AuthenticationPrincipal OAuth2User currentUser,
            // @RequestParam Long targetUserId,
            @RequestBody AddRechargePackageRequestDto request) {
        try {
            AddRechargePackageResponseDto response = walletService.addRechargePackage(request);
            return ResponseEntity.ok(MobileResponse.<AddRechargePackageResponseDto>builder()
                    .status(Boolean.TRUE).message("Package add successful ..").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<AddRechargePackageResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }
}
