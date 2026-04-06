package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RechargePackage;

@Repository
public interface RechargePackageRepository extends JpaRepository<RechargePackage, Long> {
    
    List<RechargePackage> findByIsActiveTrueOrderByOrderIndexAsc();

    Optional<RechargePackage> findByPackageIdAndIsActiveTrue(Long packageId);
}
