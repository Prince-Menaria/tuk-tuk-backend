package com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity;

public enum FollowStatus {
    ACTIVE,      // Currently following
    BLOCKED,     // Blocked by the followed user
    MUTED,       // Muted but still following
    PENDING,     // Pending approval (for private accounts)
    UNFOLLOWED   // Previously followed but unfollowed
}
