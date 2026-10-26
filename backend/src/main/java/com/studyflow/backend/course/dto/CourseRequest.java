package com.studyflow.backend.course.dto;

import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CourseRequest(

        @NotBlank(message = "Course title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Course slug is required")
        @Size(max = 220, message = "Slug cannot exceed 220 characters")
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Slug must contain lowercase letters, numbers, and single hyphens"
        )
        String slug,

        @Size(max = 10000, message = "Description cannot exceed 10000 characters")
        String description,

        @NotNull(message = "Exam ID is required")
        Long examId,

        @Size(max = 150, message = "Instructor name cannot exceed 150 characters")
        String instructorName,

        SkillLevel difficulty,

        LanguagePreference language,

        @Size(max = 500, message = "Thumbnail URL cannot exceed 500 characters")
        @Pattern(
                regexp = "^(?i:https?://).+",
                message = "Thumbnail URL must start with http:// or https://"
        )
        String thumbnailUrl,

        @Size(max = 150, message = "YouTube playlist ID cannot exceed 150 characters")
        String youtubePlaylistId

) {
}