package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.List;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.store.AddItemRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.store.PurchaseItemRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.AddItemResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.InventoryResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.PurchaseItemResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.StoreItemResponseDto;

public interface StoreService {

    List<StoreItemResponseDto> getItems(Long userId, String mainCategory, String subCategory);

    PurchaseItemResponseDto purchaseItem(PurchaseItemRequestDto request);

    List<InventoryResponseDto> getUserInventory(Long userId, String mainCategory);

    InventoryResponseDto equipItem(Long userId, Long inventoryId);

    void unequipItem(Long userId, Long inventoryId);

    AddItemResponseDto addItem(AddItemRequestDto  requestDto );

}
