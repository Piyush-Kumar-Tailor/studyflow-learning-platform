package com.studyflow.backend.curriculum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LectureRequest(

        @NotBlank(message = "Lecture title is required")
        @Size(max = 200, message = "Lecture title cannot exceed 200 characters")
        String title,

        @Size(max = 10000, message = "Description cannot exceed 10000 characters")
        String description,

        @NotNull(message = "Module ID is required")
        @Min(value = 1, message = "Module ID must be positive")
        Long moduleId,

        @NotBlank(message = "YouTube video ID is required")
        @Size(max = 50, message = "YouTube video ID cannot exceed 50 characters")
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Invalid YouTube video ID format"
        )
        String youtubeVideoId,

        @Min(value = 1, message = "Duration must be positive")
        Integer durationSeconds,

        @NotNull(message = "Sequence number is required")
        @Min(value = 1, message = "Sequence number must be at least 1")
        Integer sequenceNumber

) {
}