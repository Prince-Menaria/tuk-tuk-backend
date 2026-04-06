package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto;

import java.time.LocalDate;
import java.util.List;

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
    private String gender; // "Male" or "Female"
    private LocalDate birthday;

    private List<String> interests;
    // Q&A Answers
    private String sportsInto;
    private String musicLike;
    private String favoriteFood;
    private String favoriteMoviesTv;
    private String booksPrefer;
    private String traveled;

}
