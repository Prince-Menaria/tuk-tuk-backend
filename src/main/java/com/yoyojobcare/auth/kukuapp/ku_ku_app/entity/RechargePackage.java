package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recharge_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechargePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    @Column(nullable = false)
    private String packageName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal diamondsAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    @Builder.Default
    private Boolean isPopular = false;

    @Builder.Default
    private Boolean hasDiscount = false;

    private String badgeText;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    private Integer orderIndex;
}
