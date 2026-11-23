package com.studyflow.backend.admin.dto;

import java.util.Map;

public record AdminReviewAnalyticsResponse(
        long totalReviews,
        double averageRating,
        Map<Short, Long> ratingDistribution
) {
}