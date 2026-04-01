package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;
import java.time.LocalDateTime;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.VisitorType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_visitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVisitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitorId;

    // User whose profile was visited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_owner_id", nullable = false)
    private User profileOwner;

    // User who visited the profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_user_id", nullable = false)
    private User visitor;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    @Column(name = "visit_count", nullable = false)
    @Builder.Default
    private Integer visitCount = 1;

    @Column(name = "last_visit_at", nullable = false)
    private LocalDateTime lastVisitAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "visitor_type", nullable = false)
    @Builder.Default
    private VisitorType visitorType = VisitorType.PROFILE_VIEW;

    // Additional context
    @Column(name = "source_page", length = 100)
    private String sourcePage; // "room", "search", "recommendations", etc.

    @Column(name = "device_info", length = 500)
    private String deviceInfo;

    @PrePersist
    private void prePersist() {
        if (visitedAt == null) {
            visitedAt = LocalDateTime.now();
        }
        if (lastVisitAt == null) {
            lastVisitAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        lastVisitAt = LocalDateTime.now();
    }
}
