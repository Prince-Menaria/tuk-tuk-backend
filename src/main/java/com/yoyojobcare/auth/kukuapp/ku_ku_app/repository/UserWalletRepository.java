package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserWallet;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

        // If your User entity has field 'userId' (Long), use this:
        Optional<UserWallet> findByUserUserId(Long userId);

        boolean existsByUserUserId(Long userId);

        // Optimized for concurrency: updates with single query
        @Modifying
        @Query("UPDATE UserWallet w SET w.diamonds = w.diamonds + :amount, " +
                        "w.totalDiamondsEarned = w.totalDiamondsEarned + :amount, " +
                        "w.lastUpdated = CURRENT_TIMESTAMP " +
                        "WHERE w.user.userId = :userId")
        int addDiamondsAmount(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

        @Modifying
        @Query("UPDATE UserWallet w SET w.diamonds = w.diamonds - :amount, " +
                        "w.lastUpdated = CURRENT_TIMESTAMP " +
                        "WHERE w.user.userId = :userId AND w.diamonds >= :amount")
        int deductDiamondsAmount(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

        @Modifying
        @Query("UPDATE UserWallet w SET w.gold = w.gold + :amount, " +
                        "w.totalGoldEarned = w.totalGoldEarned + :amount, " +
                        "w.lastUpdated = CURRENT_TIMESTAMP " +
                        "WHERE w.user.userId = :userId")
        int addGoldAmount(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

        @Modifying
        @Query("UPDATE UserWallet w SET w.gold = w.gold - :amount, " +
                        "w.lastUpdated = CURRENT_TIMESTAMP " +
                        "WHERE w.user.userId = :userId AND w.gold >= :amount")
        int deductGoldAmount(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

}
