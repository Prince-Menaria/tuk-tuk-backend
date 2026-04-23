package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.StoreService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.store.AddItemRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.store.PurchaseItemRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.AddItemResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.InventoryResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.PurchaseItemResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.store.StoreItemResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StoreController {

    private final StoreService storeService;

    // ✅ Items list
    @GetMapping("/view-items")
    public ResponseEntity<MobileResponse<List<StoreItemResponseDto>>> getItems(
            @RequestParam String mainCategory,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) Long userId) {
        try {
            List<StoreItemResponseDto> items = storeService.getItems(userId, mainCategory, subCategory);
            return ResponseEntity.ok(MobileResponse.<List<StoreItemResponseDto>>builder()
                    .status(true).message("Items fetched").data(items).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<List<StoreItemResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Purchase
    @PostMapping("/purchase-item")
    public ResponseEntity<MobileResponse<PurchaseItemResponseDto>> purchaseItem(
            @RequestBody PurchaseItemRequestDto request) {
        try {
            PurchaseItemResponseDto response = storeService.purchaseItem(request);
            return ResponseEntity.ok(MobileResponse.<PurchaseItemResponseDto>builder()
                    .status(true).message(response.getMessage()).data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<PurchaseItemResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ User inventory
    @GetMapping("/view-inventory")
    public ResponseEntity<MobileResponse<List<InventoryResponseDto>>> getInventory(
            @RequestParam Long userId,
            @RequestParam(required = false) String mainCategory) {
        try {
            List<InventoryResponseDto> inventory = storeService.getUserInventory(userId, mainCategory);
            return ResponseEntity.ok(MobileResponse.<List<InventoryResponseDto>>builder()
                    .status(true).message("Inventory fetched").data(inventory).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<List<InventoryResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Equip
    @PostMapping("/inventory/{inventoryId}/equip")
    public ResponseEntity<MobileResponse<InventoryResponseDto>> equipItem(
            @PathVariable Long inventoryId,
            @RequestParam Long userId) {
        try {
            InventoryResponseDto response = storeService.equipItem(userId, inventoryId);
            return ResponseEntity.ok(MobileResponse.<InventoryResponseDto>builder()
                    .status(true).message("Item equipped").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<InventoryResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Unequip
    @PostMapping("/inventory/{inventoryId}/unequip")
    public ResponseEntity<MobileResponse<String>> unequipItem(
            @PathVariable Long inventoryId,
            @RequestParam Long userId) {
        try {
            storeService.unequipItem(userId, inventoryId);
            return ResponseEntity.ok(MobileResponse.<String>builder()
                    .status(true).message("Item unequipped").data("success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<String>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/add-store-item")
    public ResponseEntity<MobileResponse<AddItemResponseDto>> addStoreItem(
            @RequestBody AddItemRequestDto  requestDto) {
        try {
            AddItemResponseDto response = storeService.addItem(requestDto);
            return ResponseEntity.ok(MobileResponse.<AddItemResponseDto>builder()
                    .status(true).message("Add Store item successful ..").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<AddItemResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }



}
