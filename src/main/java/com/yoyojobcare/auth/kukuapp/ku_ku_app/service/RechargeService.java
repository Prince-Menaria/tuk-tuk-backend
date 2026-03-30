package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.List;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.PurchaseRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.RechargePackageDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.PurchaseResponseDto;

public interface RechargeService {
    List<RechargePackageDto> getRechargePackages();

    PurchaseResponseDto purchaseDiamonds(Long userId, PurchaseRequestDto request);

}
