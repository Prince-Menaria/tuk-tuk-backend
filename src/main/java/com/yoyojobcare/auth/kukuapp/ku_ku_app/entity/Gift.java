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
@Table(name = "gifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Gift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long giftId;

    @Column(nullable = false)
    private String giftName;

    @Column(columnDefinition = "LONGTEXT")
    private String giftImage;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal diamondCost;

    @Column(nullable = false)
    private String category; // POPULAR, LUXURY, SPECIAL, RANDOM

    private Boolean isActive = true;

    private Integer orderIndex;
    private String animationUrl; // Gift animation
}
