package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.DailyReward;

public interface DailyRewardRepository extends JpaRepository<DailyReward, Long> {

    Optional<DailyReward> findByUserIdAndClaimDate(Long userId, LocalDate claimDate);

    // Get the last claimed reward to determine consecutive days
    @Query("SELECT dr FROM DailyReward dr WHERE dr.user.userId = :userId ORDER BY dr.claimDate DESC")
    Optional<DailyReward> findLastClaimedRewardByUserId(@Param("userId") Long userId);

}
