package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.UserService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.AddRoleServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.AddUserServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.EditUserServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.ViewByUserIdServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.AddRoleServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.AddUserServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.EditUserServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.ViewAllUsersServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.ViewByUserIdServiceResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-profile")
@CrossOrigin(origins = "*")
@Tag(name = "User Profile", description = "Endpoints for all user profile related operations")
public class UserController {

    private final UserService userService;
    // private final ModelMapper modelMapper;

    @Operation(summary = "Add user Profile ", description = "Endpoint to add user profile")
    @PostMapping("/save-user")
    public ResponseEntity<MobileResponse<AddUserServiceResponseDto>> addUser(
            @RequestBody AddUserServiceRequestDto requestDto) {
        log.info("User Request ::: ", requestDto);

        AddUserServiceResponseDto serviceResponse = this.userService.saveUser(requestDto);
        if (ObjectUtils.isEmpty(serviceResponse)) {
            MobileResponse<AddUserServiceResponseDto> response = new MobileResponse<>();
            response.setData(serviceResponse);
            response.setMessage("Email already exists ");
            response.setStatus(Boolean.FALSE);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }

        MobileResponse<AddUserServiceResponseDto> response = new MobileResponse<>();
        response.setData(serviceResponse);
        response.setMessage("User save Successfull");
        response.setStatus(Boolean.TRUE);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Edit user Profile ", description = "Endpoint to edit student profile")
    @PutMapping("/edit-user")
    public ResponseEntity<MobileResponse<EditUserServiceResponseDto>> editUser(
            @RequestBody EditUserServiceRequestDto requestDto) {
        log.info("User Request ::: ", requestDto);

        EditUserServiceResponseDto serviceResponse = this.userService.updateUser(requestDto);

        MobileResponse<EditUserServiceResponseDto> response = new MobileResponse<>();
        response.setData(serviceResponse);
        response.setMessage("Edit User Successfull");
        response.setStatus(Boolean.TRUE);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "User Profile Info", description = "Endpoint to get user profile info")
    @GetMapping("/profile-info-by-id")
    public ResponseEntity<MobileResponse<ViewByUserIdServiceResponseDto>> getUserProfieInfo(
            @RequestParam Long userId) {
        ViewByUserIdServiceRequestDto requestDto = new ViewByUserIdServiceRequestDto();
        requestDto.setUserId(userId);
        log.info("User Request ::: ", requestDto);

        ViewByUserIdServiceResponseDto serviceResponse = this.userService.getUserByUserId(requestDto);

        MobileResponse<ViewByUserIdServiceResponseDto> response = new MobileResponse<>();
        response.setData(serviceResponse);
        response.setMessage("View user info ");
        response.setStatus(Boolean.TRUE);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Add user Profile ", description = "Endpoint to add user profile")
    @PostMapping("/save-roles")
    public ResponseEntity<MobileResponse<AddRoleServiceResponseDto>> addRole(
            @RequestBody AddRoleServiceRequestDto requestDto) {
        log.info("User Request ::: ", requestDto);

        AddRoleServiceResponseDto serviceResponse = this.userService.setRolesByUserIdAndRoleId(requestDto.getRoleId(), requestDto.getRoleName());
        // if (ObjectUtils.isEmpty(serviceResponse)) {
        //     MobileResponse<AddRoleServiceResponseDto> response = new MobileResponse<>();
        //     response.setData(serviceResponse);
        //     response.setMessage("Email already exists ");
        //     response.setStatus(Boolean.FALSE);
        //     return new ResponseEntity<>(response, HttpStatus.OK);

        // }

        MobileResponse<AddRoleServiceResponseDto> response = new MobileResponse<>();
        response.setData(serviceResponse);
        response.setMessage("Roles save Successful..");
        response.setStatus(Boolean.TRUE);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users-count-info")
    public ResponseEntity<MobileResponse<ViewAllUsersServiceResponseDto>> getAllActiveUsersCounts() {

        ViewAllUsersServiceResponseDto serviceResponseDto = this.userService.getAllActiveUsers();
        
        MobileResponse<ViewAllUsersServiceResponseDto> response = new MobileResponse<>();
        response.setData(serviceResponseDto);
        response.setMessage("Fetch users counts Successful..");
        response.setStatus(Boolean.TRUE);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
