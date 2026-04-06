package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.*;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.CurrencyType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.TransactionSource;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Page<WalletTransaction> findByUserUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);

    Page<WalletTransaction> findByUserUserIdAndCurrencyTypeOrderByTransactionDateDesc(Long userId,
            CurrencyType currencyType, Pageable pageable);

    List<WalletTransaction> findByUserUserIdAndSourceTypeOrderByTransactionDateDesc(Long userId,
            TransactionSource sourceType);
}
