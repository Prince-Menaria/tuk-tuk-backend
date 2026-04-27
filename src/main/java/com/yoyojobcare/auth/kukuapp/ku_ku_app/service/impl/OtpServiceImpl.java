package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.OtpVerification;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Provider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Role;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.OtpVerificationRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RoleRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.security.JwtTokenProvider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.IdGenerator;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.OtpService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.otp.SendOtpRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.otp.VerifyOtpRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.otp.OtpLoginResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.otp.SendOtpResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final IdGenerator idGenerator;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

    @Override
    public SendOtpResponseDto sendOtp(SendOtpRequestDto request) {
        try {
            log.info("📱 OTP request for mobile: {}", request.getMobile());

            String mobile = this.sanitizeMobile(request.getCountryCode(), request.getMobile());

            // ✅ Rate limiting — last 1 minute mein OTP bheja?
            Optional<OtpVerification> recentOtp = otpRepository
                    .findTopByMobileAndIsUsedFalseOrderByCreatedAtDesc(mobile);

            if (recentOtp.isPresent()) {
                LocalDateTime sentAt = recentOtp.get().getCreatedAt() != null
                        ? LocalDateTime.ofInstant(recentOtp.get().getCreatedAt(),
                                java.time.ZoneId.systemDefault())
                        : LocalDateTime.now().minusMinutes(2);

                if (sentAt.isAfter(LocalDateTime.now().minusMinutes(1))) {
                    throw new RuntimeException("OTP already sent. Please wait 1 minute before resending.");
                }
            }

            // ✅ OTP generate karo
            String otpCode = generateOtp();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

            // ✅ Purane unused OTPs invalidate karo
            otpRepository.findByMobileOrderByCreatedAtDesc(mobile)
                    .forEach(o -> {
                        o.setIsUsed(true);
                        otpRepository.save(o);
                    });

            // ✅ Naya OTP save karo
            OtpVerification otp = new OtpVerification();
            otp.setMobile(mobile);
            otp.setOtpCode(otpCode);
            otp.setExpiresAt(expiresAt);
            otp.setIsUsed(false);
            otp.setAttempts(0);
            otp.setPurpose("LOGIN");

            this.otpRepository.save(otp);

            // ✅ SMS bhejo
            this.sendSms(mobile, otpCode);

            log.info("✅ OTP sent to: {}", mobile);

            return SendOtpResponseDto.builder()
                    .success(true)
                    .message("OTP sent successfully to " + maskMobile(request.getMobile()))
                    .mobile(maskMobile(request.getMobile()))
                    .expiresInSeconds(OTP_EXPIRY_MINUTES * 60)   // Means 5 Minutes valid hai 
                    .build();
        } catch (Exception e) {
            log.error("Error in occur Send otp ", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OtpLoginResponseDto verifyOtpAndLogin(VerifyOtpRequestDto request) {
        try {
            log.info("🔐 OTP verify for mobile: {}", request.getMobile());

            String mobile = sanitizeMobile(request.getCountryCode(), request.getMobile());

            // ✅ Latest OTP fetch karo
            OtpVerification otp = otpRepository
                    .findTopByMobileAndIsUsedFalseOrderByCreatedAtDesc(mobile)
                    .orElse(null);
                    

            if(ObjectUtils.isEmpty(otp)){
                OtpLoginResponseDto rs = new OtpLoginResponseDto();
                rs.setMessage("OTP not found. Please request a new OTP.");
                rs.setSuccess(Boolean.FALSE);
                return rs;
            }        

            // ✅ Attempts check
            if (otp.getAttempts() >= MAX_ATTEMPTS) {
                otp.setIsUsed(true);
                otpRepository.save(otp);
                throw new RuntimeException("Too many wrong attempts. Please request a new OTP.");
            }

            // ✅ Expiry check
            if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
                otp.setIsUsed(true);
                otpRepository.save(otp);
                throw new RuntimeException("OTP expired. Please request a new OTP.");
            }

            // ✅ OTP match check
            if (!otp.getOtpCode().equals(request.getOtpCode().trim())) {
                otp.setAttempts(otp.getAttempts() + 1);
                otpRepository.save(otp);
                int remaining = MAX_ATTEMPTS - otp.getAttempts();
                throw new RuntimeException("Wrong OTP. " + remaining + " attempts remaining.");
            }

            // ✅ OTP mark used
            otp.setIsUsed(true);
            otpRepository.save(otp);

            // ✅ User find or create
            Long mobileNumber = Long.parseLong(request.getMobile().replaceAll("[^0-9]", ""));
            boolean isNewUser = false;

            User user = userRepository.findByMobile(mobileNumber).orElse(null);

            if (user == null) {
                // ✅ Naya user banao
                isNewUser = true;
                user = createMobileUser(mobile, mobileNumber);
                log.info("🆕 New user created via OTP: {}", mobile);
            } else {
                log.info("✅ Existing user login: {}", user.getUserId());
            }

            // ✅ JWT tokens generate karo
            String accessToken = this.jwtTokenProvider.generateAccessToken(user);
            String refreshToken = this.jwtTokenProvider.generateRefreshToken(user);

            return OtpLoginResponseDto.builder()
                    .success(true)
                    .message(isNewUser ? "Welcome! Please complete your profile." : "Login successful!")
                    .isNewUser(isNewUser)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getUserId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .image(user.getImage())
                    .build();
        } catch (Exception e) {
            log.error("Error in occur verify otp and login ", e);
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Mobile user create
    private User createMobileUser(String fullMobile, Long mobileNumber) {
        User user = new User();
        user.setUserId(idGenerator.generate6DigitUserId());
        user.setMobile(mobileNumber);
        user.setFullName("User_" + mobileNumber.toString().substring(mobileNumber.toString().length() - 4));
        user.setProvider(Provider.PHONE);
        user.setEnable(true);

        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("USER");
                    return roleRepository.save(r);
                });
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    // ✅ 6 digit OTP
    private String generateOtp() {
        // Production mein real random use karo
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    // ✅ SMS send — Twilio/MSG91/2Factor integration
    private void sendSms(String mobile, String otpCode) {
        // TODO: Real SMS gateway integrate karo
        // Abhi sirf log karo — development ke liye
        log.info("📨 [SMS MOCK] OTP {} sent to {}", otpCode, mobile);

        // ✅ Twilio example:
        // Message.creator(new PhoneNumber(mobile),
        //     new PhoneNumber(twilioFromNumber),
        //     "Your TukTuk OTP is: " + otpCode + ". Valid for 5 minutes.")
        //     .create();
    }

    // ✅ Mobile sanitize — "+91" + "9876543210" → "+919876543210"
    private String sanitizeMobile(String countryCode, String mobile) {
        String code = (countryCode != null && !countryCode.isBlank())
                ? countryCode.trim() : "+91";
        String num = mobile.replaceAll("[^0-9]", "");
        return code + num;
    }

    // ✅ Mask — "9876543210" → "98****3210"
    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 6) return mobile;
        return mobile.substring(0, 2) + "****" + mobile.substring(mobile.length() - 4);
    }

}
