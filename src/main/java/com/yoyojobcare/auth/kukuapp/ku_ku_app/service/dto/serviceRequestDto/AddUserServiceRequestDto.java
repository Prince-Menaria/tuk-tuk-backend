package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AddUserServiceRequestDto {

    private String fullName;
    private String picture;
    private String email;
    private String gender;
    private LocalDate birthday;


     

}
