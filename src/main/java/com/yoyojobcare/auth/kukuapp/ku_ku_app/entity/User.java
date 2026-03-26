package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private Long mobile;
    private String password;
    private String image;
    private String role; // "ADMIN" or "User"
    private boolean enable = Boolean.TRUE;

    private String education;
    private String school;
    private String occupation;
    private String spokenLanguage;
    private String aboutMe;
    private String gender;         // "Male" or "Female"
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInterest> interests;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserAnswers answers;

    


    

}
