package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_answers")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAnswers extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String sportsInto;         // "What sports are you into?"
    private String musicLike;          // "What music do you like?"
    private String favoriteFood;       // "What's your favorite food?"
    private String favoriteMoviesTv;   // "Favorite movies and TV shows?"
    private String booksPrefer;        // "What books do you prefer?"
    private String traveled;

}
