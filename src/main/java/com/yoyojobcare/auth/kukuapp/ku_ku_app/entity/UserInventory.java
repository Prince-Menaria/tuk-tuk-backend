package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserInventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private StoreItem item;

    @Column(nullable = false, precision = 15, scale = 2)
    private java.math.BigDecimal pricePaid;

    @Column(nullable = false)
    private String currencyUsed; // DIAMOND, GOLD

    private Boolean isActive = true; // ✅ Equip/Unequip ke liye

    private Boolean isEquipped = false;

    // ✅ Expiry tracking
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;   // null = permanent

    private Boolean isExpired = false;
}
