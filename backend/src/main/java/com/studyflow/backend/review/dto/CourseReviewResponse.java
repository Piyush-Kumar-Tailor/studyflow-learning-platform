package com.studyflow.backend.review.dto;

import java.time.LocalDateTime;

public record CourseReviewResponse(

        Long id,

        Long courseId,

        String courseTitle,

        Long userId,

        String reviewerName,

        Short rating,

        String reviewText,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}