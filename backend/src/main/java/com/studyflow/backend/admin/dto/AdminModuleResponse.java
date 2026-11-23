package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminModuleResponse(
        Long id,
        Long courseId,
        String courseTitle,
        String title,
        String description,
        Integer sequenceNumber,
        boolean published,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}