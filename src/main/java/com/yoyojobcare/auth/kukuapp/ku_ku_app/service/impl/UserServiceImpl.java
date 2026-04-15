package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Role;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserAnswers;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserFollow;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserInterest;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.FollowStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RoleRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserAnswersRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserFollowRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserInterestRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.IdGenerator;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.UserService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.AddUserServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.DeleteUserByUserIdServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.EditUserServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.ViewAllActiveUsersProfileServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.ViewByUserIdServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.AddRoleServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.AddUserServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.DeleteUserByUserIdServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.EditUserServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.ViewAllActiveUsersProfileServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.ViewAllUsersServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.ViewByUserIdServiceResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional  // ✅ Class level pe @Transactional add karo
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserInterestRepository userInterestRepository;
    private final UserAnswersRepository userAnswersRepository;
    private final IdGenerator idGenerator;
    private final RoleRepository roleRepository;
    private final UserFollowRepository followRepository;

    @Override
    public AddUserServiceResponseDto saveUser(AddUserServiceRequestDto serviceRequestDto) {
        // log.info();
        try {
            User userAlready = userRepository.findByEmail(serviceRequestDto.getEmail()).orElse(null);
            if (!ObjectUtils.isEmpty(userAlready)) {
                return null;
            }
            User saveUser = new User();
            saveUser.setUserId(this.idGenerator.generate6DigitUserId());
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
    @Transactional
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

            // ✅ Update Interests
            if (serviceRequestDto.getInterests() != null) {
                this.updateUserInterests(updatedUser.getUserId(), serviceRequestDto.getInterests());
            }
            // ✅ Update Q&A Answers
            this.updateUserAnswers(updatedUser, serviceRequestDto);

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
        try {
            User user = userRepository.findById(serviceRequestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ Delete related data first
            userInterestRepository.deleteByUserUserId(user.getUserId());
            userAnswersRepository.deleteByUserUserId(user.getUserId());

            // Delete user (wallet will be cascade deleted due to OneToOne mapping)
            userRepository.delete(user);

            DeleteUserByUserIdServiceResponseDto response = new DeleteUserByUserIdServiceResponseDto();
            // response.setUserId(user.getUserId());
            // response.setMessage("User deleted successfully");

            log.info("User deleted successfully for id: {}", user.getUserId());
            return response;
        } catch (Exception e) {
            log.error("Error in deleteUserByUserId(): {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

   
    private void updateUserInterests(Long userId, List<String> interests) {
        try {
            // Delete existing interests
            this.userInterestRepository.deleteByUserUserId(userId);

            // Add new interests
            if (interests != null && !interests.isEmpty()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                List<UserInterest> userInterests = interests.stream()
                        .map(interest -> {
                            UserInterest userInterest = new UserInterest();
                            userInterest.setUser(user);
                            userInterest.setInterest(interest);
                            return userInterest;
                        })
                        .collect(Collectors.toList());

                userInterestRepository.saveAll(userInterests);
                log.info("Updated {} interests for user: {}", interests.size(), userId);
            }
        } catch (Exception e) {
            log.error("Error updating interests for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update interests: " + e.getMessage());
        }
    }

   
    private void updateUserAnswers(User user, EditUserServiceRequestDto requestAnswers) {
        try {
            UserAnswers answers = userAnswersRepository.findByUserUserId(user.getUserId())
                    .orElse(new UserAnswers());

            // Set user if it's a new answers entity
            if (answers.getId() == null) {
                answers.setUser(user);
            }

            // Update answers if provided
            if (requestAnswers.getSportsInto() != null)
                answers.setSportsInto(requestAnswers.getSportsInto());
            if (requestAnswers.getMusicLike() != null)
                answers.setMusicLike(requestAnswers.getMusicLike());
            if (requestAnswers.getFavoriteFood() != null)
                answers.setFavoriteFood(requestAnswers.getFavoriteFood());
            if (requestAnswers.getFavoriteMoviesTv() != null)
                answers.setFavoriteMoviesTv(requestAnswers.getFavoriteMoviesTv());
            if (requestAnswers.getBooksPrefer() != null)
                answers.setBooksPrefer(requestAnswers.getBooksPrefer());
            if (requestAnswers.getTraveled() != null)
                answers.setTraveled(requestAnswers.getTraveled());

            userAnswersRepository.save(answers);
            log.info("Updated answers for user: {}", user.getUserId());
        } catch (Exception e) {
            log.error("Error updating answers for user {}: {}", user.getUserId(), e.getMessage());
            throw new RuntimeException("Failed to update answers: " + e.getMessage());
        }
    }

    @Override
    public AddRoleServiceResponseDto setRolesByUserIdAndRoleId(Long roleId, String newRoleName) {
        try {

            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

            // ✅ Duplicate name check
            boolean nameExists = roleRepository.findByName(newRoleName).isPresent();
            if (nameExists) {
                throw new RuntimeException("Role name already exists: " + newRoleName);
            }

            role.setName(newRoleName);
            Role save = roleRepository.save(role);

            AddRoleServiceResponseDto r = new AddRoleServiceResponseDto();
            r.setRoleName(Set.of(save.getName()));

            return r;

            // // ✅ User find karo
            // User user = this.userRepository.findById(userId)
            // .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // // ✅ Role DB se fetch karo — naya mat banao
            // Role role = this.roleRepository.findById(roleId)
            // .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

            // // ✅ Role already hai to dobara add mat karo
            // boolean alreadyExists = user.getRoles().stream()
            // .anyMatch(r -> r.getRoleId().equals(roleId));

            // if (!alreadyExists) {
            // user.getRoles().add(role);
            // }

            // // ✅ Save karo aur return karo
            // User saveRole = userRepository.save(user);
            // Set<Role> roles = saveRole.getRoles();
            // roles.stream()
            // .map(e -> {
            // Set<String> setRoles = new HashSet<>();
            // setRoles.add(e.getName());
            // AddRoleServiceResponseDto r = new AddRoleServiceResponseDto();
            // r.setRoleName(setRoles);
            // return r;

            // });

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ViewAllUsersServiceResponseDto getAllActiveUsers() {
        try {
            Long userCounts = 0L;
            int size = this.userRepository.findAll().size();
            userCounts = Long.valueOf(size);
            ViewAllUsersServiceResponseDto rs = new ViewAllUsersServiceResponseDto();
            rs.setUserCounts(userCounts);
            return rs;

        } catch (Exception e) {
            log.error("Error occur view list active users", e);
            throw e;
        }
    }

    @Override
    public List<ViewAllActiveUsersProfileServiceResponseDto> getAllActiveUsersProfile(
            ViewAllActiveUsersProfileServiceRequestDto serviceRequestDto) {
        try {
            List<User> listUsers = this.userRepository.findAll()
                    .stream()
                    .filter(e -> Boolean.TRUE.equals(e.isEnable()))
                    .limit(10)
                    .collect(Collectors.toList());

            Map<Long, UserFollow> userFollowMap = this.followRepository
                    .findByFollowerUserIdAndFollowStatus(serviceRequestDto.getCurrentUserId(),
                            FollowStatus.ACTIVE)
                    .stream()
                    .collect(Collectors.toMap(
                            follow -> follow.getFollowing().getUserId(), // key
                            Function.identity() // value
                    ));

            List<ViewAllActiveUsersProfileServiceResponseDto> listResponse = listUsers.stream().map(e -> {
                ViewAllActiveUsersProfileServiceResponseDto rs = new ViewAllActiveUsersProfileServiceResponseDto();
                rs.setUserId(e.getUserId());
                rs.setFullName(e.getFullName());
                rs.setProfileImage(e.getImage());

                UserFollow userFollow = userFollowMap.getOrDefault(serviceRequestDto.getCurrentUserId(), null);
                Boolean isFollowing = Boolean.FALSE;
                if (!ObjectUtils.isEmpty(userFollow)) {
                    isFollowing = userFollow.getFollowStatus().equals(FollowStatus.ACTIVE);
                    rs.setIsFollowing(isFollowing);

                }
                return rs;
            }).collect(Collectors.toList());

            return listResponse;
        } catch (Exception e) {
            log.error("Error occur view All Active Users Profile", e);
            throw e;
        }
    }

}
