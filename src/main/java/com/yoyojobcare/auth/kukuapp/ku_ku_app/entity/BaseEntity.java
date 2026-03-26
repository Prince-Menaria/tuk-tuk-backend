package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.time.Instant;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@MappedSuperclass
@Data
public class BaseEntity {

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null)
            createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
