package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.StoreItem;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserInventory;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserWallet;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.WalletTransaction;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.StoreItemRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserInventoryRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserWalletRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.WalletTransactionRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.StoreService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.store.AddItemRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.store.PurchaseItemRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.AddItemResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.InventoryResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.PurchaseItemResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.StoreItemResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StoreServiceImpl implements StoreService {

    private final StoreItemRepository storeItemRepository;
    private final UserInventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final UserWalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StoreItemResponseDto> getItems(Long userId, String mainCategory, String subCategory) {
        try {
            log.info("🛒 Store items: category={}, sub={}, user={}", mainCategory, subCategory, userId);

            List<StoreItem> items;
            if (subCategory != null && !subCategory.isBlank()) {
                items = storeItemRepository
                        .findByMainCategoryAndSubCategoryAndIsActiveTrueOrderByOrderIndexAsc(
                                mainCategory.toUpperCase(), subCategory.toUpperCase());
            } else {
                items = storeItemRepository
                        .findByMainCategoryAndIsActiveTrueOrderByOrderIndexAsc(
                                mainCategory.toUpperCase());
            }

            return items.stream().map(item -> {
                boolean isOwned = userId != null &&
                        inventoryRepository.existsByUserUserIdAndItemItemIdAndIsExpiredFalse(
                                userId, item.getItemId());

                // ✅ Equipped check
                boolean isEquipped = false;
                if (isOwned) {
                    isEquipped = inventoryRepository
                            .findEquippedByCategory(userId, item.getMainCategory())
                            .map(inv -> inv.getItem().getItemId().equals(item.getItemId()))
                            .orElse(false);
                }

                StoreItemResponseDto storeItemResponseDto = StoreItemResponseDto.builder()
                        .itemId(item.getItemId())
                        .itemName(item.getItemName())
                        .mainCategory(item.getMainCategory())
                        .subCategory(item.getSubCategory())
                        .itemImage(item.getItemImage())
                        .price(item.getPrice())
                        .currency(item.getCurrency())
                        .durationDays(item.getDurationDays())
                        .durationLabel(formatDuration(item.getDurationDays()))
                        .level(item.getLevel())
                        .isOwned(isOwned)
                        .isEquipped(isEquipped)
                        .build();
                return storeItemResponseDto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error in Get Store items : ", e);
            throw e;
        }
    }

    @Override
    public PurchaseItemResponseDto purchaseItem(PurchaseItemRequestDto request) {
        log.info("🛒 Purchase: user={}, item={}", request.getUserId(), request.getItemId());
        try {
            User user = this.userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            StoreItem item = this.storeItemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            if (!item.getIsActive())
                throw new RuntimeException("Item not available");

            // ✅ Already owned check
            boolean alreadyOwned = this.inventoryRepository
                    .existsByUserUserIdAndItemItemIdAndIsExpiredFalse(
                            request.getUserId(), request.getItemId());
            if (alreadyOwned)
                throw new RuntimeException("Item already owned");

            // ✅ Wallet fetch
            UserWallet wallet = walletRepository.findByUserUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            BigDecimal price = item.getPrice();
            boolean isDiamond = "DIAMOND".equalsIgnoreCase(item.getCurrency());

            // ✅ Balance check
            if (isDiamond && wallet.getDiamonds().compareTo(price) < 0) {
                throw new RuntimeException("Insufficient diamonds. Required: "
                        + price + ", Available: " + wallet.getDiamonds());
            }
            if (!isDiamond && wallet.getGold().compareTo(price) < 0) {
                throw new RuntimeException("Insufficient gold. Required: "
                        + price + ", Available: " + wallet.getGold());
            }

            // ✅ Deduct wallet
            BigDecimal prevBalance = isDiamond ? wallet.getDiamonds() : wallet.getGold();
            if (isDiamond)
                wallet.deductDiamonds(price);
            else
                wallet.deductGold(price);
            walletRepository.save(wallet);
            BigDecimal newBalance = isDiamond ? wallet.getDiamonds() : wallet.getGold();

            // ✅ Wallet transaction
            WalletTransaction txn = WalletTransaction.builder()
                    .user(user)
                    .currencyType(isDiamond ? CurrencyType.DIAMONDS : CurrencyType.GOLD)
                    .transactionType(TransactionType.DEBIT)
                    .amount(price)
                    .balanceBefore(prevBalance)
                    .balanceAfter(newBalance)
                    .sourceType(TransactionSource.PURCHASE)
                    .sourceDescription("Purchased store item: " + item.getItemName())
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.COMPLETED)
                    .build();
            walletTransactionRepository.save(txn);

            // ✅ Inventory save
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = item.getDurationDays() > 0
                    ? now.plusDays(item.getDurationDays())
                    : null;

            UserInventory inventory = new UserInventory();
            inventory.setUser(user);
            inventory.setItem(item);
            inventory.setPricePaid(price);
            inventory.setCurrencyUsed(item.getCurrency());
            inventory.setIsActive(true);
            inventory.setIsEquipped(false);
            inventory.setPurchasedAt(now);
            inventory.setExpiresAt(expiresAt);
            inventory.setIsExpired(false);

            UserInventory saved = inventoryRepository.save(inventory);
            log.info("✅ Item purchased: inventory={}", saved.getInventoryId());

            return PurchaseItemResponseDto.builder()
                    .success(true)
                    .message(item.getItemName() + " purchased successfully!")
                    .inventoryId(saved.getInventoryId())
                    .itemName(item.getItemName())
                    .pricePaid(price)
                    .currencyUsed(item.getCurrency())
                    .remainingDiamonds(wallet.getDiamonds())
                    .remainingGold(wallet.getGold())
                    .expiresAt(expiresAt)
                    .build();

        } catch (Exception e) {
            log.error("Purchase store items error: {}", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getUserInventory(Long userId, String mainCategory) {
        try {
            return inventoryRepository.findActiveInventory(userId)
                    .stream()
                    .filter(inv -> mainCategory == null
                            || inv.getItem().getMainCategory().equalsIgnoreCase(mainCategory))
                    .map(inv -> InventoryResponseDto.builder()
                            .inventoryId(inv.getInventoryId())
                            .itemId(inv.getItem().getItemId())
                            .itemName(inv.getItem().getItemName())
                            .mainCategory(inv.getItem().getMainCategory())
                            .subCategory(inv.getItem().getSubCategory())
                            .itemImage(inv.getItem().getItemImage())
                            .isEquipped(inv.getIsEquipped())
                            .purchasedAt(inv.getPurchasedAt())
                            .expiresAt(inv.getExpiresAt())
                            .isExpired(inv.getIsExpired())
                            .durationLabel(formatDuration(inv.getItem().getDurationDays()))
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("get User Inventory error: {}", e);
            throw e;
        }
    }

    @Override
    public InventoryResponseDto equipItem(Long userId, Long inventoryId) {
        try {
            UserInventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory item not found"));

            if (!inventory.getUser().getUserId().equals(userId))
                throw new RuntimeException("Not authorized");

            // ✅ Same category ka pehle equipped item unequip karo
            String category = inventory.getItem().getMainCategory();
            inventoryRepository.findEquippedByCategory(userId, category)
                    .ifPresent(prev -> {
                        prev.setIsEquipped(false);
                        inventoryRepository.save(prev);
                    });

            inventory.setIsEquipped(true);
            inventoryRepository.save(inventory);

            return InventoryResponseDto.builder()
                    .inventoryId(inventory.getInventoryId())
                    .itemId(inventory.getItem().getItemId())
                    .itemName(inventory.getItem().getItemName())
                    .isEquipped(true)
                    .build();
        } catch (Exception e) {
            log.error("equip Item error: {}", e);
            throw e;
        }
    }

    @Override
    public void unequipItem(Long userId, Long inventoryId) {
        try {
            UserInventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory item not found"));
            if (!inventory.getUser().getUserId().equals(userId))
                throw new RuntimeException("Not authorized");
            inventory.setIsEquipped(false);
            inventoryRepository.save(inventory);
        } catch (Exception e) {
            log.error("unequip Item error: {}", e);
            throw e;
        }
    }

    @Override
    public AddItemResponseDto addItem(AddItemRequestDto requestDto) {
        try {
            log.info("Request in add item {} ", requestDto);

            StoreItem item = new StoreItem();
            item.setItemName(requestDto.getItemName());
            item.setItemImage(requestDto.getItemImage());
            item.setMainCategory(requestDto.getMainCategory());
            item.setPrice(requestDto.getPrice());
            item.setSubCategory(requestDto.getSubCategory());
            item.setCurrency(requestDto.getCurrency());
            item.setDurationDays(requestDto.getDurationDays());

            StoreItem saveItem = this.storeItemRepository.save(item);
            
            AddItemResponseDto serviceResponseDto = new AddItemResponseDto();
            serviceResponseDto.setItemId(saveItem.getItemId());

            return serviceResponseDto;
        } catch (Exception e) {
            log.error("Error in Add Item : ", e);
            throw e;
        }
    }

    private String formatDuration(Integer days) {
        if (days == null || days == 0)
            return "Permanent";
        if (days == 7)
            return "7 Days";
        if (days == 30)
            return "30 Days";
        if (days == 90)
            return "90 Days";
        if (days == 365)
            return "1 Year";
        return days + " Days";
    }

}
