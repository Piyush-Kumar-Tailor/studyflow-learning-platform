package com.studyflow.backend.notification.service;

import com.studyflow.backend.admin.dto.AdminAnnouncementRequest;
import com.studyflow.backend.notification.dto.CreateNotificationRequest;
import com.studyflow.backend.notification.dto.NotificationResponse;
import com.studyflow.backend.notification.dto.NotificationUnreadCountResponse;
import com.studyflow.backend.notification.entity.Notification;
import com.studyflow.backend.notification.repository.NotificationRepository;
import com.studyflow.backend.user.entity.AppUser;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager;

    public NotificationService(
            NotificationRepository notificationRepository,
            EntityManager entityManager
    ) {
        this.notificationRepository = notificationRepository;
        this.entityManager = entityManager;
    }

    // Create a notification for a user
    public NotificationResponse createNotification(
            Long userId,
            CreateNotificationRequest request
    ) {
        Notification notification = new Notification(
                entityManager.getReference(AppUser.class, userId),
                request.title().trim(),
                request.message().trim(),
                request.type().trim().toUpperCase(),
                request.referenceId()
        );

        return toResponse(notificationRepository.save(notification));
    }

    // Get all notifications of a user
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(
            Long userId,
            Pageable pageable
    ) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    // Get unread notifications
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUnreadNotifications(
            Long userId,
            Pageable pageable
    ) {
        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(this::toResponse);
    }

    // Get unread notification count
    @Transactional(readOnly = true)
    public NotificationUnreadCountResponse getUnreadCount(Long userId) {

        long count = notificationRepository.countByUserIdAndReadFalse(userId);

        return new NotificationUnreadCountResponse(count);
    }

    // Mark a single notification as read
    public NotificationResponse markAsRead(
            Long userId,
            Long notificationId
    ) {
        Notification notification = getUserNotification(
                notificationId,
                userId
        );

        notification.markAsRead();

        return toResponse(notificationRepository.save(notification));
    }

    // Mark all notifications as read
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    // Delete a notification
    public void deleteNotification(
            Long userId,
            Long notificationId
    ) {
        Notification notification = getUserNotification(
                notificationId,
                userId
        );

        notificationRepository.delete(notification);
    }

    // Find notification belonging to the authenticated user
    private Notification getUserNotification(
            Long notificationId,
            Long userId
    ) {
        return notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"
                ));
    }

    // Convert entity to response DTO
    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getReferenceId(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    public int sendAnnouncementToAllStudents(
            AdminAnnouncementRequest request,
            List<AppUser> students
    ) {
        if (students == null || students.isEmpty()) {
            return 0;
        }

        List<Notification> notifications = students.stream()
                .map(student -> new Notification(
                        student,
                        request.title().trim(),
                        request.message().trim(),
                        request.type().trim().toUpperCase(),
                        null
                ))
                .toList();

        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    public NotificationResponse sendNotificationToStudent(
            Long userId,
            String title,
            String message,
            String type
    ) {
        Notification notification = new Notification(
                entityManager.getReference(AppUser.class, userId),
                title.trim(),
                message.trim(),
                type.trim().toUpperCase(),
                null
        );

        return toResponse(notificationRepository.save(notification));
    }
}