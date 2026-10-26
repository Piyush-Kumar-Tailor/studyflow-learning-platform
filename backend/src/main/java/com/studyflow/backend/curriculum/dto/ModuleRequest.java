package com.studyflow.backend.curriculum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModuleRequest(

        @NotBlank(message = "Module title is required")
        @Size(max = 200, message = "Module title cannot exceed 200 characters")
        String title,

        @Size(max = 10000, message = "Description cannot exceed 10000 characters")
        String description,

        @NotNull(message = "Course ID is required")
        @Min(value = 1, message = "Course ID must be positive")
        Long courseId,

        @NotNull(message = "Sequence number is required")
        @Min(value = 1, message = "Sequence number must be at least 1")
        Integer sequenceNumber

) {
}