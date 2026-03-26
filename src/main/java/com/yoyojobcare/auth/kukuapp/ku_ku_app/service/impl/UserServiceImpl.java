package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserInterest;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserAnswersRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserInterestRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.UserService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.AddUserServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.DeleteUserByUserIdServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.EditUserServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.ViewByUserIdServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.AddUserServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.DeleteUserByUserIdServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.EditUserServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.ViewByUserIdServiceResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserInterestRepository userInterestRepository;
    private final UserAnswersRepository userAnswersRepository;

    @Override
    public AddUserServiceResponseDto saveUser(AddUserServiceRequestDto serviceRequestDto) {
        // log.info();
        try {
            User saveUser = new User();
            saveUser.setEmail(serviceRequestDto.getEmail());
            saveUser.setImage(serviceRequestDto.getPicture());
            saveUser.setFullName(serviceRequestDto.getFullName());
            saveUser.setGender(serviceRequestDto.getGender());
            saveUser.setBirthday(serviceRequestDto.getBirthday());

            User savedUser = userRepository.save(saveUser);
            log.info("User saved successfully with id: {}", savedUser.getUserId());

            User save = this.userRepository.save(saveUser);
            AddUserServiceResponseDto response = new AddUserServiceResponseDto();
            response.setUserid(save.getUserId());
            return response;
        } catch (Exception e) {
            log.error("Error in saveUser(): {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user: " + e.getMessage());
        }
    }

    @Override
    public EditUserServiceResponseDto updateUser(EditUserServiceRequestDto serviceRequestDto) {
        try {
            User updateUser = userRepository.findById(serviceRequestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("user not found"));
            // Only update fields if they are not null (partial update support)
            if (serviceRequestDto.getEmail() != null)
                updateUser.setEmail(serviceRequestDto.getEmail());
            if (serviceRequestDto.getPicture() != null)
                updateUser.setImage(serviceRequestDto.getPicture());
            if (serviceRequestDto.getFullName() != null)
                updateUser.setFullName(serviceRequestDto.getFullName());
            if (serviceRequestDto.getGender() != null)
                updateUser.setGender(serviceRequestDto.getGender());
            if (serviceRequestDto.getBirthday() != null)
                updateUser.setBirthday(serviceRequestDto.getBirthday());
            if (serviceRequestDto.getEducation() != null)
                updateUser.setEducation(serviceRequestDto.getEducation());
            if (serviceRequestDto.getOccupation() != null)
                updateUser.setOccupation(serviceRequestDto.getOccupation());
            if (serviceRequestDto.getSpokenLanguage() != null)
                updateUser.setSpokenLanguage(serviceRequestDto.getSpokenLanguage());
            if (serviceRequestDto.getAboutMe() != null)
                updateUser.setAboutMe(serviceRequestDto.getAboutMe());

            // ✅ save() was missing before — without this DB update nahi hoga
            User updatedUser = userRepository.save(updateUser);
            log.info("User updated successfully for id: {}", updatedUser.getUserId());

            EditUserServiceResponseDto response = new EditUserServiceResponseDto();
            response.setUserId(updateUser.getUserId());

            return response;
        } catch (Exception e) {
            log.error("Error in updateUser(): {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    public ViewByUserIdServiceResponseDto getUserByUserId(ViewByUserIdServiceRequestDto serviceRequestDto) {
        try {
            User getUser = userRepository.findById(serviceRequestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("user not found"));

            // ✅ Removed UnsupportedOperationException — properly mapping now
            ViewByUserIdServiceResponseDto responseDto = new ViewByUserIdServiceResponseDto();
            responseDto.setUserId(getUser.getUserId());
            responseDto.setEmail(getUser.getEmail());
            responseDto.setFullName(getUser.getFullName());
            responseDto.setPicture(getUser.getImage());
            responseDto.setGender(getUser.getGender());
            responseDto.setBirthday(getUser.getBirthday());
            responseDto.setEducation(getUser.getEducation());
            responseDto.setOccupation(getUser.getOccupation());
            responseDto.setSpokenLanguage(getUser.getSpokenLanguage());
            responseDto.setAboutMe(getUser.getAboutMe());

            // Interests fetch — method name update karo
            List<UserInterest> interests = userInterestRepository.findByUserUserId(getUser.getUserId());
            List<String> interestNames = interests.stream()
                    .map(UserInterest::getInterest)
                    .collect(Collectors.toList());
            responseDto.setInterests(interestNames);

            // Fetch Q&A answers
            userAnswersRepository.findByUserUserId(getUser.getUserId())
                    .ifPresent(answers -> {
                        responseDto.setSportsInto(answers.getSportsInto());
                        responseDto.setMusicLike(answers.getMusicLike());
                        responseDto.setFavoriteFood(answers.getFavoriteFood());
                        responseDto.setFavoriteMoviesTv(answers.getFavoriteMoviesTv());
                        responseDto.setBooksPrefer(answers.getBooksPrefer());
                        responseDto.setTraveled(answers.getTraveled());
                    });

            log.info("User fetched successfully for id: {}", getUser.getUserId());
            return responseDto;
        } catch (Exception e) {
            log.error("Error in getUserByUserId(): {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch user: " + e.getMessage());
        }
    }

    @Override
    public DeleteUserByUserIdServiceResponseDto deleteUserByUserId(
            DeleteUserByUserIdServiceRequestDto serviceRequestDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUserByUserId'");
    }

}
