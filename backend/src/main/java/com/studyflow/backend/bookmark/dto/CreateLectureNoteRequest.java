package com.studyflow.backend.bookmark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLectureNoteRequest(

        @NotBlank(message = "Note title is required")
        @Size(max = 200, message = "Note title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Note content is required")
        @Size(max = 50000, message = "Note content cannot exceed 50000 characters")
        String content

) {
}