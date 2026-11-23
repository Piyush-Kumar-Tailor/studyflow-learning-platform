package com.studyflow.backend.admin.dto;

import com.studyflow.backend.enrollment.enums.EnrollmentStatus;

import java.time.LocalDateTime;

public record AdminEnrollmentResponse(
        Long enrollmentId,
        Long studentId,
        Long courseId,
        String courseTitle,
        EnrollmentStatus status,
        LocalDateTime enrolledAt,
        LocalDateTime completedAt
) {
}