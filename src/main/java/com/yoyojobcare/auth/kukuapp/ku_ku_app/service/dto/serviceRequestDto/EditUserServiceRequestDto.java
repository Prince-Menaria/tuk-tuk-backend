package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EditUserServiceRequestDto {

    private Long userId;
    private String fullName;
    private String picture;
    private String email;

    private String education;
    private String school;
    private String occupation;
    private String spokenLanguage;
    private String aboutMe;
    private String gender;         // "Male" or "Female"
    private LocalDate birthday;

}
