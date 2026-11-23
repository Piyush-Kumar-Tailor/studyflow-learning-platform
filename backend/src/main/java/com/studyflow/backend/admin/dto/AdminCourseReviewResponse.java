package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminCourseReviewResponse(
        Long reviewId,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentName,
        String studentEmail,
        Short rating,
        String reviewText,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}