package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Notification;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // ✅ User ki notifications — latest first
    // @Query("""
    //     SELECT n FROM Notification n
    //     LEFT JOIN FETCH n.sender
    //     WHERE n.receiver.userId = :userId
    //     AND n.isRead = :isRead
    //     ORDER BY n.notifiedAt DESC
    //     """)
    Page<Notification> findByReceiverUserIdAndIsReadOrderByNotifiedAtDesc(Long userId, Boolean isRead, Pageable pageable);

    // @Query("""
    //     SELECT n FROM Notification n
    //     LEFT JOIN FETCH n.sender
    //     WHERE n.receiver.userId = :userId
    //     ORDER BY n.notifiedAt DESC
    //     """)
    // ✅ All notifications for user — latest first
    Page<Notification> findByReceiverUserIdOrderByNotifiedAtDesc(Long userId, Pageable pageable);

    // // ✅ Type se filter
    // @Query("""
    //     SELECT n FROM Notification n
    //     LEFT JOIN FETCH n.sender
    //     WHERE n.receiver.userId = :userId
    //     AND n.type IN :types
    //     ORDER BY n.notifiedAt DESC
    //     """)
   // ✅ Type se filter — latest first
    Page<Notification> findByReceiverUserIdAndTypeInOrderByNotifiedAtDesc(Long userId, List<String> types, Pageable pageable);

    // // ✅ Unread count
    long countByReceiverUserIdAndIsReadFalse(Long receiverId);

    // // ✅ Mark all read
    // @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.userId = :userId")
    void markAllAsRead(@Param("userId") Long userId);

}
