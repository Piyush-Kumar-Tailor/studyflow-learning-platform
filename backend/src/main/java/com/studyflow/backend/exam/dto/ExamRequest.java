package com.studyflow.backend.exam.dto;

import com.studyflow.backend.user.enums.ExamCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExamRequest(

        @NotBlank(message = "Exam name is required")
        @Size(max = 150, message = "Exam name cannot exceed 150 characters")
        String name,

        @NotBlank(message = "Exam code is required")
        @Size(max = 50, message = "Exam code cannot exceed 50 characters")
        @Pattern(
                regexp = "^[A-Za-z0-9][A-Za-z0-9._-]*$",
                message = "Exam code can contain letters, numbers, dots, underscores, and hyphens"
        )
        String code,

        @NotNull(message = "Exam category is required")
        ExamCategory category,

        @Size(max = 150, message = "Conducting body cannot exceed 150 characters")
        String conductingBody,

        @Size(max = 10000, message = "Description cannot exceed 10000 characters")
        String description,

        @Size(max = 500, message = "Official website URL cannot exceed 500 characters")
        @Pattern(
                regexp = "^(?i:https?://).+",
                message = "Official website must start with http:// or https://"
        )
        String officialWebsite
) {
}