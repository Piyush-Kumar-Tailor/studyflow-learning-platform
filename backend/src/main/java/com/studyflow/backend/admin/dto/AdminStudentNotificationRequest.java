package com.studyflow.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminStudentNotificationRequest(

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Message is required")
        String message,

        @NotBlank(message = "Notification type is required")
        @Size(max = 50, message = "Notification type cannot exceed 50 characters")
        String type

) {
}