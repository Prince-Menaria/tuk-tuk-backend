package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.WalletStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // Assuming 'User' entity exists

    @Column(name = "diamonds", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal diamonds = BigDecimal.ZERO;

    @Column(name = "gold", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal gold = BigDecimal.ZERO;

    // Total earned for lifetime stats (Screenshot 1)
    @Column(name = "total_diamonds_earned", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalDiamondsEarned = BigDecimal.ZERO;

    @Column(name = "total_gold_earned", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalGoldEarned = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_status", nullable = false)
    @Builder.Default
    private WalletStatus walletStatus = WalletStatus.ACTIVE;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PreUpdate
    @PrePersist
    private void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }

    // --- Helper Methods ---
    public void addDiamonds(BigDecimal amount) {
        this.diamonds = this.diamonds.add(amount);
        this.totalDiamondsEarned = this.totalDiamondsEarned.add(amount);
    }

    public boolean deductDiamonds(BigDecimal amount) {
        if (this.diamonds.compareTo(amount) >= 0) {
            this.diamonds = this.diamonds.subtract(amount);
            return true;
        }
        return false;
    }

    public void addGold(BigDecimal amount) {
        this.gold = this.gold.add(amount);
        this.totalGoldEarned = this.totalGoldEarned.add(amount);
    }

    public boolean deductGold(BigDecimal amount) {
        if (this.gold.compareTo(amount) >= 0) {
            this.gold = this.gold.subtract(amount);
            return true;
        }
        return false;
    }

    public boolean hasSufficientDiamonds(BigDecimal amount) {
        return this.diamonds.compareTo(amount) >= 0;
    }

    public boolean hasSufficientGold(BigDecimal amount) {
        return this.gold.compareTo(amount) >= 0;
    }

}
