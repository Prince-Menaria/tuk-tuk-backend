package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.WalletService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.AddCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.DeductCurrencyRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.GetTransactionsRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.CurrencyOperationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletBalanceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.WalletTransactionHistoryResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    /**
     * Get user's wallet balance (Matches Screenshot 1 top-left, 4 top-left)
     */
    @GetMapping("/balance")
    public ResponseEntity<MobileResponse<WalletBalanceResponseDto>> getWalletBalance(
            @RequestParam Long userId) {

        try {
            log.info("Getting wallet balance for user: {}", userId);

            WalletBalanceResponseDto response = walletService.getUserWallet(userId);

            MobileResponse<WalletBalanceResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(true);
            mobileResponse.setMessage("Wallet balance retrieved successfully");
            mobileResponse.setData(response);

            return ResponseEntity.ok(mobileResponse);

        } catch (Exception e) {
            log.error("Error getting wallet balance: {}", e.getMessage());

            MobileResponse<WalletBalanceResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get transaction history (Matches Screenshot 2, 3 details pages)
     */
    @GetMapping("/transactions")
    public ResponseEntity<MobileResponse<WalletTransactionHistoryResponseDto>> getTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String currencyType,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam Long userId) {

        try {
            
            log.info("Getting transaction history for user: {}", userId);

            GetTransactionsRequestDto request = new GetTransactionsRequestDto();
            request.setPage(page);
            request.setSize(size);
            request.setCurrencyType(currencyType);
            request.setSortBy(sortBy);
            request.setSortDirection(sortDirection);

            WalletTransactionHistoryResponseDto response = walletService.getTransactionHistory(userId, request);

            MobileResponse<WalletTransactionHistoryResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(true);
            mobileResponse.setMessage("Transaction history retrieved successfully");
            mobileResponse.setData(response);

            return ResponseEntity.ok(mobileResponse);

        } catch (Exception e) {
            log.error("Error getting transaction history: {}", e.getMessage());

            MobileResponse<WalletTransactionHistoryResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // --- Admin/Internal Endpoints (Protected for internal use) ---
    /**
     * Add currency to wallet (Admin/Internal use, e.g., task rewards, referral
     * bonuses)
     */
    @PostMapping("/add")
    public ResponseEntity<MobileResponse<CurrencyOperationResponseDto>> addCurrency(
            @RequestParam Long targetUserId, // User to whom currency is added
            @RequestBody AddCurrencyRequestDto request) {

        try {
            // Validate if currentUser has permission to add currency to targetUserId
            // currentUserService.validateAdminOrSystemUser(currentUser);

            log.info("Admin/System adding {} {} to user {}", request.getAmount(), request.getCurrencyType(),
                    targetUserId);

            CurrencyOperationResponseDto response = walletService.addCurrency(targetUserId, request);

            MobileResponse<CurrencyOperationResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(response.getSuccess());
            mobileResponse.setMessage(response.getMessage());
            mobileResponse.setData(response);

            return ResponseEntity.ok(mobileResponse);

        } catch (Exception e) {
            log.error("Error adding currency: {}", e.getMessage());

            MobileResponse<CurrencyOperationResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Deduct currency from wallet (Admin/Internal use, e.g., penalties)
     */
    @PostMapping("/deduct")
    public ResponseEntity<MobileResponse<CurrencyOperationResponseDto>> deductCurrency(
            @RequestParam Long targetUserId, // User from whom currency is deducted
            @RequestBody DeductCurrencyRequestDto request) {

        try {
            // Validate if currentUser has permission to deduct currency from targetUserId
            // currentUserService.validateAdminOrSystemUser(currentUser);

            log.info("Admin/System deducting {} {} from user {}", request.getAmount(), request.getCurrencyType(),
                    targetUserId);

            CurrencyOperationResponseDto response = walletService.deductCurrency(targetUserId, request);

            MobileResponse<CurrencyOperationResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(response.getSuccess());
            mobileResponse.setMessage(response.getMessage());
            mobileResponse.setData(response);

            return ResponseEntity.ok(mobileResponse);

        } catch (Exception e) {
            log.error("Error deducting currency: {}", e.getMessage());

            MobileResponse<CurrencyOperationResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Create wallet for user (if not exists) - can be called on user registration
     */
    @PostMapping("/create")
    public ResponseEntity<MobileResponse<WalletBalanceResponseDto>> createWallet(
            @RequestParam Long userId) {

        try {
            log.info("Creating wallet for user: {}", userId);

            WalletBalanceResponseDto response = walletService.createWalletForUser(userId);

            MobileResponse<WalletBalanceResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(true);
            mobileResponse.setMessage("Wallet created successfully");
            mobileResponse.setData(response);

            return ResponseEntity.ok(mobileResponse);

        } catch (Exception e) {
            log.error("Error creating wallet: {}", e.getMessage());

            MobileResponse<WalletBalanceResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
