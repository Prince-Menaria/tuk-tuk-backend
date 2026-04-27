package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.OtpVerification;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    // ✅ Latest unused OTP fetch karo
    Optional<OtpVerification> findTopByMobileAndIsUsedFalseOrderByCreatedAtDesc(String mobile);

    // ✅ Purane OTPs delete karo
    void deleteByMobileAndIsUsedTrue(String mobile);

    // ✅ Mobile se all OTPs
    List<OtpVerification> findByMobileOrderByCreatedAtDesc(String mobile);

}
