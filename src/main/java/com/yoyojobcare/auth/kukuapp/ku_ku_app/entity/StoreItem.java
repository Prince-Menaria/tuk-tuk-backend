package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StoreItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String itemName;

    // ✅ Main category: INTIMACY, CHAT_FRAME, AVATAR_FRAME, ENTER_EFFECT, ROOM_THEME
    @Column(nullable = false)
    private String mainCategory;

    // ✅ Sub category (sirf Intimacy ke liye): FRAME, JEWELLERY, MIC, CAR, BUBBLE, ENTRANCE
    private String subCategory;

    @Column(columnDefinition = "LONGTEXT")
    private String itemImage;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    // ✅ Currency: DIAMOND, GOLD
    @Column(nullable = false)
    private String currency = "DIAMOND";

    // ✅ Duration: 7, 30, 90, 365 (days), 0 = permanent
    private Integer durationDays = 7;

    // ✅ Level: D, E, C, B, A
    private String level;

    private Boolean isActive = true;

    private Integer orderIndex;

    private String description;
}
