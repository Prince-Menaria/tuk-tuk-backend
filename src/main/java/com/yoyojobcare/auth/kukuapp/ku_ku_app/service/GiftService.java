package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.List;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift.AddGiftRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift.SendGiftRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift.ViewReceivedAllGiftUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift.ViewSendedAllGiftUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.AddGiftResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.SendGiftResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.ViewGiftResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.ViewReceivedAllGiftUserResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.ViewRoomGiftResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.ViewSendedAllGiftUserResponseDto;

public interface GiftService {

    SendGiftResponseDto sendGift(SendGiftRequestDto request);

    List<ViewGiftResponseDto> getAllGifts();

    List<ViewGiftResponseDto> getGiftsByCategory(String category);

    List<ViewRoomGiftResponseDto> getRoomGiftHistory(Long roomId);

    AddGiftResponseDto addGift(AddGiftRequestDto serviceRequest);

    List<ViewReceivedAllGiftUserResponseDto> getReceivedAllGiftUserByReceiverId(ViewReceivedAllGiftUserRequestDto requestDto);

    List<ViewSendedAllGiftUserResponseDto> getSendedAllGiftUserBySenderId(ViewSendedAllGiftUserRequestDto requestDto);

}
