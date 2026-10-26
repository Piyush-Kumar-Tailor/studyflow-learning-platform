package com.studyflow.backend.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CreateCourseReviewRequest(

        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot exceed 5")
        Short rating,

        @Size(max = 5000, message = "Review cannot exceed 5000 characters")
        String reviewText

) {
}