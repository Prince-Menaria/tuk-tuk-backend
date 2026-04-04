package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.IdGenerator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdGeneratorImpl implements IdGenerator {

    private final UserRepository userRepository;

    @Override
    public Long generate6DigitUserId() {
        Long id;
        do {
            id = 100000L + (long) (Math.random() * 900000);
        } while (userRepository.existsById(id)); //
        return id;
    }

    // @Override
    // public Long generate6DigitUserId() {
    //     SecureRandom random = new SecureRandom();

    //     // ✅ Ek baar saare existing IDs fetch karo
    //     Set<Long> existingIds = userRepository.findAllUserIds();

    //     Long id;
    //     do {
    //         id = 100000L + (long) (random.nextInt(900000));
    //     } while (existingIds.contains(id)); // ✅ DB call nahi, Set mein check

    //     return id;
    // }

}
