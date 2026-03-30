package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet.ExchangeRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.ExchangeRatesDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet.ExchangeResponseDto;

public interface ExchangeService {
    ExchangeResponseDto exchangeDiamondsToGold(Long userId, ExchangeRequestDto request);

    ExchangeRatesDto getExchangeRates();

}
