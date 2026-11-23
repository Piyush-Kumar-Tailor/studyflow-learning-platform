package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminNotificationResponse(
        Long notificationId,
        Long studentId,
        String studentName,
        String studentEmail,
        String title,
        String message,
        String type,
        Long referenceId,
        boolean read,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
}