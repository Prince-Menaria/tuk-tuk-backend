package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long roleId; 
    @Column(unique = true, nullable = false)
    private String name;

}
