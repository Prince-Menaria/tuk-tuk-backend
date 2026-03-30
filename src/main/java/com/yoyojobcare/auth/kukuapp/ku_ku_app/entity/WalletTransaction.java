package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Assuming 'User' entity exists

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type", nullable = false)
    private CurrencyType currencyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private TransactionSource sourceType;

    @Column(name = "source_description", length = 500)
    private String sourceDescription;

    @Column(name = "reference_id", length = 100, unique = true)
    private String referenceId; // Unique identifier for each transaction

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.COMPLETED;

    @PrePersist
    private void prePersist() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
        if (referenceId == null) {
            referenceId = generateReferenceId();
        }
    }

    private String generateReferenceId() {
        // Example: DIAMONDS_1_1678888888888 (CurrencyType_UserId_Timestamp)
        return String.format("%s_%d_%d",
                currencyType.name(),
                user.getUserId(),
                System.currentTimeMillis());
    }

}
