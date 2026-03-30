package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.ExchangeService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.ExchangeRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.ExchangeRatesDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.ExchangeResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
@Slf4j
public class ExchangeController {

    private final ExchangeService exchangeService;

    /**
     * 💱 Exchange diamonds to gold (Matches Screenshot 5: "Exchange" dialog)
     */
    @PostMapping("/diamonds-to-gold")
    public ResponseEntity<MobileResponse<ExchangeResponseDto>> exchangeDiamondsToGold(
            @RequestParam Long userId,
            @Valid @RequestBody ExchangeRequestDto request) {
        
        try {
            log.info("💱 User {} exchanging {} diamonds to gold", userId, request.getDiamondAmount());
            
            ExchangeResponseDto response = exchangeService.exchangeDiamondsToGold(userId, request);
            
            MobileResponse<ExchangeResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(response.getSuccess());
            mobileResponse.setMessage(response.getMessage());
            mobileResponse.setData(response);
            
            return ResponseEntity.ok(mobileResponse);
            
        } catch (Exception e) {
            log.error("❌ Exchange error: {}", e.getMessage());
            
            MobileResponse<ExchangeResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 📊 Get exchange rates (Information to populate exchange dialog)
     */
    @GetMapping("/rates")
    public ResponseEntity<MobileResponse<ExchangeRatesDto>> getExchangeRates() {
        
        try {
            ExchangeRatesDto rates = exchangeService.getExchangeRates();
            
            MobileResponse<ExchangeRatesDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setStatus(true);
            mobileResponse.setMessage("Exchange rates retrieved successfully");
            mobileResponse.setData(rates);
            
            return ResponseEntity.ok(mobileResponse);
            
        } catch (Exception e) {
            log.error("❌ Error getting exchange rates: {}", e.getMessage());
            
            MobileResponse<ExchangeRatesDto> errorResponse = new MobileResponse<>();
            errorResponse.setStatus(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
