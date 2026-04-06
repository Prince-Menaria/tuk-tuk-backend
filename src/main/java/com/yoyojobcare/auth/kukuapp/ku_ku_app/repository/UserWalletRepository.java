package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserWallet;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    Optional<UserWallet> findByUserUserId(Long userId);

    boolean existsByUserUserId(Long userId);

}
