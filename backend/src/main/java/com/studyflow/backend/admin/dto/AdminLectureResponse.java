package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminLectureResponse(
        Long id,
        Long moduleId,
        String moduleTitle,
        Long courseId,
        String courseTitle,
        String title,
        String description,
        String youtubeVideoId,
        Integer durationSeconds,
        Integer sequenceNumber,
        boolean published,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}