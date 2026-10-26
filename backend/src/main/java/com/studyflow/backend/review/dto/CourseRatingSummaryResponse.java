package com.studyflow.backend.review.dto;

public record CourseRatingSummaryResponse(
        Long courseId,
        long totalReviews,
        double averageRating
) {
}