package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.GiftService;
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
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/gifts")
@RequiredArgsConstructor
@Slf4j
public class GiftController {

    private final GiftService giftService;

    // ✅ Gift send karo
    @PostMapping("/send")
    public ResponseEntity<MobileResponse<SendGiftResponseDto>> sendGift(
            @RequestBody SendGiftRequestDto request) {
        try {
            SendGiftResponseDto response = giftService.sendGift(request);
            return ResponseEntity.ok(MobileResponse.<SendGiftResponseDto>builder()
                    .status(true).message("Gift sent successfully").data(response).build());
        } catch (Exception e) {
            log.error("Send gift error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MobileResponse.<SendGiftResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Add Gift
    @PostMapping("/add-gift")
    public ResponseEntity<MobileResponse<AddGiftResponseDto>> addGift(
            @RequestBody AddGiftRequestDto request) {
        try {
            AddGiftResponseDto response = giftService.addGift(request);
            return ResponseEntity.ok(MobileResponse.<AddGiftResponseDto>builder()
                    .status(true).message("Gift add successfully").data(response).build());
        } catch (Exception e) {
            log.error("Send gift error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MobileResponse.<AddGiftResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Saare gifts list
    @GetMapping("/list")
    public ResponseEntity<MobileResponse<List<ViewGiftResponseDto>>> getAllGifts() {
        List<ViewGiftResponseDto> gifts = giftService.getAllGifts();
        return ResponseEntity.ok(MobileResponse.<List<ViewGiftResponseDto>>builder()
                .status(true).message("All Gifts fetched successful.. ").data(gifts).build());
    }

    // ✅ Category se gifts
    @GetMapping("/category")
    public ResponseEntity<MobileResponse<List<ViewGiftResponseDto>>> getGiftsByCategory(
            @RequestParam String category) {
        List<ViewGiftResponseDto> gifts = giftService.getGiftsByCategory(category);
        return ResponseEntity.ok(MobileResponse.<List<ViewGiftResponseDto>>builder()
                .status(true).message("Gifts fetched").data(gifts).build());
    }

    // ✅ Room gift history
    @GetMapping("/room-history")
    public ResponseEntity<MobileResponse<List<ViewRoomGiftResponseDto>>> getRoomGiftHistory(
            @RequestParam Long roomId) {
        List<ViewRoomGiftResponseDto> history = giftService.getRoomGiftHistory(roomId);
        return ResponseEntity.ok(MobileResponse.<List<ViewRoomGiftResponseDto>>builder()
                .status(true).message("Gift history fetched successful..").data(history).build());
    }

    @GetMapping("/view-user-gift-received")
    public ResponseEntity<MobileResponse<List<ViewReceivedAllGiftUserResponseDto>>> getReceivedAllGiftUserByReceiverId(
            @RequestParam Long receiverId) {

        ViewReceivedAllGiftUserRequestDto requestDto = new ViewReceivedAllGiftUserRequestDto();
        requestDto.setReceiverId(receiverId);
        List<ViewReceivedAllGiftUserResponseDto> listServiceResponse = giftService.getReceivedAllGiftUserByReceiverId(requestDto);
        return ResponseEntity.ok(MobileResponse.<List<ViewReceivedAllGiftUserResponseDto>>builder()
                .status(true).message("User all received gift fetched successful..").data(listServiceResponse).build());
    }

    @GetMapping("/view-user-gift-send")
    public ResponseEntity<MobileResponse<List<ViewSendedAllGiftUserResponseDto>>> getSendedAllGiftUserBySenderId(
            @RequestParam Long senderId) {

        ViewSendedAllGiftUserRequestDto requestDto = new ViewSendedAllGiftUserRequestDto();
        requestDto.setSenderId(senderId);
        List<ViewSendedAllGiftUserResponseDto> listServiceResponse = giftService.getSendedAllGiftUserBySenderId(requestDto);
        return ResponseEntity.ok(MobileResponse.<List<ViewSendedAllGiftUserResponseDto>>builder()
                .status(true).message("User all received gift fetched successful..").data(listServiceResponse).build());
    }

}
