package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ViewByUserIdServiceResponseDto {

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

    private String sportsInto; // "What sports are you into?"
    private String musicLike; // "What music do you like?"
    private String favoriteFood; // "What's your favorite food?"
    private String favoriteMoviesTv; // "Favorite movies and TV shows?"
    private String booksPrefer; // "What books do you prefer?"
    private String traveled;

}
