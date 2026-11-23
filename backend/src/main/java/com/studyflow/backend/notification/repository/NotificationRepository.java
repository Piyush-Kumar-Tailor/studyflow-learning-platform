package com.studyflow.backend.notification.repository;

import com.studyflow.backend.notification.entity.Notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Page<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Optional<Notification> findByIdAndUserId(
            Long notificationId,
            Long userId
    );

    Page<Notification> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    // Count all notifications.
    long count();

    // Count read notifications.
    long countByReadTrue();

    // Count unread notifications.
    long countByReadFalse();


    long countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.read = true,
                n.readAt = CURRENT_TIMESTAMP
            WHERE n.user.id = :userId
              AND n.read = false
            """)
    int markAllAsRead(@Param("userId") Long userId);

    @Query("""
    SELECT n.type, COUNT(n)
    FROM Notification n
    GROUP BY n.type
    ORDER BY n.type
    """)
    List<Object[]> countNotificationsByType();
}