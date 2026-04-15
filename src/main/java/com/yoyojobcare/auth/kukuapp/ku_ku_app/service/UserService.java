package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.List;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
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

public interface UserService {

    public AddUserServiceResponseDto saveUser(AddUserServiceRequestDto serviceRequestDto); 

    public EditUserServiceResponseDto updateUser(EditUserServiceRequestDto serviceRequestDto); 

    public ViewByUserIdServiceResponseDto getUserByUserId(ViewByUserIdServiceRequestDto serviceRequestDto); 

    public DeleteUserByUserIdServiceResponseDto deleteUserByUserId(DeleteUserByUserIdServiceRequestDto serviceRequestDto); 

    public AddRoleServiceResponseDto setRolesByUserIdAndRoleId(Long roleId, String roleName);

    public ViewAllUsersServiceResponseDto getAllActiveUsers();

    public List<ViewAllActiveUsersProfileServiceResponseDto> getAllActiveUsersProfile(
            ViewAllActiveUsersProfileServiceRequestDto serviceRequestDto);

    

     

}
