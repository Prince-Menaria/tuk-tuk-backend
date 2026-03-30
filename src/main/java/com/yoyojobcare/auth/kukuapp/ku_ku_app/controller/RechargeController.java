package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.RechargeService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.PurchaseRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.RechargePackageDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.PurchaseResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/recharge")
@RequiredArgsConstructor
@Slf4j
public class RechargeController {

    private final RechargeService rechargeService;

    /**
     * 📋 Get recharge packages (Matches Screenshot 1: "Recharge Diamonds" section)
     */
    @GetMapping("/packages")
    public ResponseEntity<MobileResponse<List<RechargePackageDto>>> getRechargePackages() {
        
        try {
            List<RechargePackageDto> packages = rechargeService.getRechargePackages();
            
            MobileResponse<List<RechargePackageDto>> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(true);
            mobileResponse.setMessage("Recharge packages retrieved successfully");
            mobileResponse.setData(packages);
            
            return ResponseEntity.ok(mobileResponse);
            
        } catch (Exception e) {
            log.error("❌ Error getting recharge packages: {}", e.getMessage());
            
            MobileResponse<List<RechargePackageDto>> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 💳 Purchase diamonds (Matches Screenshot 1: "Recharge INR 56 Now" button)
     */
    @PostMapping("/purchase")
    public ResponseEntity<MobileResponse<PurchaseResponseDto>> purchaseDiamonds(
            @RequestParam Long userId,
            @RequestBody PurchaseRequestDto request) {
        
        try {
            log.info("💳 User {} purchasing package {}", userId, request.getPackageId());
            
            PurchaseResponseDto response = rechargeService.purchaseDiamonds(userId, request);
            
            MobileResponse<PurchaseResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(response.getSuccess());
            mobileResponse.setMessage(response.getMessage());
            mobileResponse.setData(response);
            
            return ResponseEntity.ok(mobileResponse);
            
        } catch (Exception e) {
            log.error("❌ Purchase error: {}", e.getMessage());
            
            MobileResponse<PurchaseResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
