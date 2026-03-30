package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.WalletTransaction;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);

    Page<WalletTransaction> findByUserIdAndCurrencyTypeOrderByTransactionDateDesc(Long userId,
            CurrencyType currencyType, Pageable pageable);

    List<WalletTransaction> findByUserIdAndSourceTypeOrderByTransactionDateDesc(Long userId,
            TransactionSource sourceType);

    Optional<WalletTransaction> findByReferenceId(String referenceId);

}
