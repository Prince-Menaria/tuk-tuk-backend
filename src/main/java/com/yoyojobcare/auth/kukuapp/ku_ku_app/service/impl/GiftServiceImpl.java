package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.ChatRoom;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Gift;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.GiftTransaction;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserWallet;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.WalletTransaction;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.ChatRoomRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.GiftRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.GiftTransactionRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserWalletRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.WalletTransactionRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.GiftService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift.AddGiftRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift.SendGiftRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.gift.ViewReceivedAllGiftUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.AddGiftResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.SendGiftResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.ViewGiftResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.gift.ViewReceivedAllGiftUserResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftServiceImpl implements GiftService {

    private final GiftRepository giftRepository;
    private final GiftTransactionRepository giftTransactionRepository;
    private final UserRepository userRepository;
    private final UserWalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public SendGiftResponseDto sendGift(SendGiftRequestDto request) {
        log.info("🎁 Gift send request: sender={}, receiver={}, gift={}, qty={}",
                request.getSenderId(), request.getReceiverId(),
                request.getGiftId(), request.getQuantity());
        try {
            // ✅ Validate karo
            User sender = userRepository.findById(request.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            User receiver = userRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            Gift gift = giftRepository.findById(request.getGiftId())
                    .orElseThrow(() -> new RuntimeException("Gift not found"));

            if (!gift.getIsActive()) {
                throw new RuntimeException("Gift is not available");
            }

            int quantity = request.getQuantity() != null && request.getQuantity() > 0
                    ? request.getQuantity()
                    : 1;

            // ✅ Total cost calculate karo
            BigDecimal totalCost = gift.getDiamondCost().multiply(BigDecimal.valueOf(quantity));

            // ✅ Sender ka wallet fetch karo
            UserWallet senderWallet = walletRepository.findByUserUserId(request.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

            // ✅ Balance check karo
            if (senderWallet.getDiamonds().compareTo(totalCost) < 0) {
                throw new RuntimeException("Insufficient diamonds. Required: " + totalCost
                        + ", Available: " + senderWallet.getDiamonds());
            }

            // ✅ Diamonds deduct karo sender se
            BigDecimal previousBalance = senderWallet.getDiamonds();
            senderWallet.deductDiamonds(totalCost);
            walletRepository.save(senderWallet);

            // ✅ Wallet transaction record karo
            WalletTransaction walletTxn = WalletTransaction.builder()
                    .user(sender)
                    .currencyType(CurrencyType.DIAMONDS)
                    .transactionType(TransactionType.DEBIT)
                    .amount(totalCost)
                    .balanceBefore(previousBalance)
                    .balanceAfter(senderWallet.getDiamonds())
                    .sourceType(TransactionSource.GIFT_SENT)
                    .sourceDescription("Sent " + quantity + "x " + gift.getGiftName()
                            + " to " + receiver.getFullName())
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.COMPLETED)
                    .build();
            walletTransactionRepository.save(walletTxn);

            // ✅ Receiver ko gold credit karo (optional — business logic)
            // BigDecimal goldReward = totalCost.multiply(BigDecimal.valueOf(0.7));
            // receiverWallet.addGold(goldReward);

            // ✅ Gift transaction save karo
            ChatRoom room = null;
            if (request.getRoomId() != null) {
                room = chatRoomRepository.findById(request.getRoomId()).orElse(null);
            }

            GiftTransaction giftTxn = new GiftTransaction();
            giftTxn.setSender(sender);
            giftTxn.setReceiver(receiver);
            giftTxn.setGift(gift);
            giftTxn.setRoom(room);
            giftTxn.setQuantity(quantity);
            giftTxn.setTotalDiamondsCost(totalCost);
            giftTxn.setSentAt(LocalDateTime.now());

            GiftTransaction savedTxn = giftTransactionRepository.save(giftTxn);

            // ✅ WebSocket se real-time broadcast karo room mein
            if (room != null) {
                SendGiftResponseDto giftEvent = buildGiftResponse(savedTxn, senderWallet.getDiamonds());
                messagingTemplate.convertAndSend(
                        "/topic/room/" + room.getRoomId() + "/gifts", giftEvent);
                log.info("🎁 Gift broadcast to room {}", room.getRoomId());
            }

            log.info("✅ Gift sent successfully: txn={}", savedTxn.getGiftTransactionId());
            return buildGiftResponse(savedTxn, senderWallet.getDiamonds());

        } catch (Exception e) {
            log.error("❌ Send gift error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ViewGiftResponseDto> getAllGifts() {
        try {
            return this.giftRepository.findByIsActiveTrueOrderByOrderIndexAsc()
                    .stream().map(this::toGiftDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occur view all gifts ..", e);
            throw e;
        }
    }

    @Override
    public List<ViewGiftResponseDto> getGiftsByCategory(String category) {
        try {
            log.info("Request of get Gifts By Category :: " + category);
            return giftRepository.findByCategoryAndIsActiveTrueOrderByOrderIndexAsc(category)
                    .stream().map(this::toGiftDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occur get Gifts By Category ..", e);
            throw e;
        }
    }

    @Override
    public List<SendGiftResponseDto> getRoomGiftHistory(Long roomId) {
        try {
            log.info("Request of get Room Gift History using roomId {} :: " + roomId);
            return giftTransactionRepository.findByRoomRoomIdOrderBySentAtDesc(roomId)
                    .stream().map(t -> buildGiftResponse(t, null))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occur get Room Gift History ..", e);
            throw e;
        }
    }

    @Override
    public AddGiftResponseDto addGift(AddGiftRequestDto serviceRequest) {
        try {
            log.info("Request of add gift {} :: " + serviceRequest);

            Gift saveGift = new Gift();
            saveGift.setGiftName(serviceRequest.getGiftName());
            saveGift.setGiftImage(serviceRequest.getGiftImage());
            saveGift.setDiamondCost(serviceRequest.getDiamondCost());
            saveGift.setAnimationUrl(serviceRequest.getAnimationUrl());
            saveGift.setCategory(serviceRequest.getCategory());
            saveGift.setOrderIndex(serviceRequest.getOrderIndex());

            Gift storeGift = this.giftRepository.save(saveGift);

            AddGiftResponseDto addGiftResponseDto = new AddGiftResponseDto();
            addGiftResponseDto.setGiftId(storeGift.getGiftId());

            return addGiftResponseDto;
        } catch (Exception e) {
            log.error("Error occur add gift ..", e);
            throw e;
        }
    }

    @Override
    public List<ViewReceivedAllGiftUserResponseDto> getReceivedAllGiftUser(ViewReceivedAllGiftUserRequestDto serviceRequest) {
        try {
            log.info("Request of get Received All Gift User {} :: " + serviceRequest);


            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'receivedGiftUser'");
        } catch (Exception e) {
            log.error("Error occur get Received All Gift User ..", e);
            throw e;
        }
    }

    // ✅ Helper
    private ViewGiftResponseDto toGiftDto(Gift gift) {
        return ViewGiftResponseDto.builder()
                .giftId(gift.getGiftId())
                .giftName(gift.getGiftName())
                .giftImage(gift.getGiftImage())
                .diamondCost(gift.getDiamondCost())
                .category(gift.getCategory())
                .animationUrl(gift.getAnimationUrl())
                .orderIndex(gift.getOrderIndex())
                .build();
    }

    private SendGiftResponseDto buildGiftResponse(GiftTransaction txn, BigDecimal remainingDiamonds) {
        return SendGiftResponseDto.builder()
                .success(true)
                .message(txn.getSender().getFullName() + " sent " + txn.getQuantity()
                        + "x " + txn.getGift().getGiftName())
                .giftTransactionId(txn.getGiftTransactionId())
                .giftId(txn.getGift().getGiftId())
                .giftName(txn.getGift().getGiftName())
                .giftImage(txn.getGift().getGiftImage())
                .animationUrl(txn.getGift().getAnimationUrl())
                .senderId(txn.getSender().getUserId())
                .senderName(txn.getSender().getFullName())
                .senderImage(txn.getSender().getImage())
                .receiverId(txn.getReceiver().getUserId())
                .receiverName(txn.getReceiver().getFullName())
                .quantity(txn.getQuantity())
                .totalDiamondsCost(txn.getTotalDiamondsCost())
                .senderRemainingDiamonds(remainingDiamonds)
                .sentAt(txn.getSentAt())
                .build();
    }

}
