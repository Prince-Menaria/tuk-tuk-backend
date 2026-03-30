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
    private BigDecimal diamondsAmount; // e.g., 3600 (for 3.6K)

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price; // e.g., 56.00 (INR)

    @Column(nullable = false, length = 10)
    private String currency = "INR";

    private String description;
    
    @Builder.Default
    private Boolean isPopular = false;
    
    @Builder.Default
    private Boolean hasDiscount = false;

    @Column(precision = 15, scale = 2)
    private BigDecimal originalPrice;

    private Integer discountPercentage;

    private String badgeText; // e.g., "+24.0K" for bonus diamonds
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    private Integer orderIndex; // For display order

}
