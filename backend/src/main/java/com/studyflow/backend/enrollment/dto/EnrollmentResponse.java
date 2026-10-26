package com.studyflow.backend.enrollment.dto;

import com.studyflow.backend.enrollment.entity.CourseEnrollment;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long courseId,
        String courseTitle,
        String courseSlug,
        String thumbnailUrl,
        EnrollmentStatus status,
        LocalDateTime enrolledAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EnrollmentResponse from(CourseEnrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getCourse().getSlug(),
                enrollment.getCourse().getThumbnailUrl(),
                enrollment.getStatus(),
                enrollment.getEnrolledAt(),
                enrollment.getCompletedAt(),
                enrollment.getCreatedAt(),
                enrollment.getUpdatedAt()
        );
    }
}