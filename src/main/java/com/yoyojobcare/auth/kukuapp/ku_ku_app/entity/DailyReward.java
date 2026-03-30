package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Assuming 'User' entity exists

    @Column(nullable = false)
    private LocalDate claimDate; // Date when the reward was claimed

    @Column(nullable = false)
    private Integer consecutiveDays; // How many consecutive days claimed

    @Column(nullable = false)
    private LocalDateTime claimedAt;

    @Column(nullable = false)
    private CurrencyType rewardCurrencyType;

    @Column(nullable = false)
    private BigDecimal rewardAmount;

    // Optional: could add a reference to the WalletTransaction
    @Column(name = "transaction_id")
    private Long transactionId;

    @PrePersist
    private void prePersist() {
        if (claimedAt == null) {
            claimedAt = LocalDateTime.now();
        }
    }

}
