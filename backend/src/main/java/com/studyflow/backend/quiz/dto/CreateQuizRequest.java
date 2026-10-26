package com.studyflow.backend.quiz.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateQuizRequest(

        @NotNull(message = "Course ID is required")
        Long courseId,

        Long lectureId,

        @NotBlank(message = "Quiz title is required")
        @Size(max = 200, message = "Quiz title cannot exceed 200 characters")
        String title,

        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 600, message = "Duration cannot exceed 600 minutes")
        Integer durationMinutes,

        @NotNull(message = "Passing percentage is required")
        @DecimalMin(value = "0.00", message = "Passing percentage cannot be negative")
        @DecimalMax(value = "100.00", message = "Passing percentage cannot exceed 100")
        BigDecimal passingPercentage
) {
}