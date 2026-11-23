package com.studyflow.backend.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Message is required")
        @Size(max = 10000, message = "Message cannot exceed 10000 characters")
        String message,

        @NotBlank(message = "Notification type is required")
        @Size(max = 50, message = "Type cannot exceed 50 characters")
        String type,

        Long referenceId

) {
}