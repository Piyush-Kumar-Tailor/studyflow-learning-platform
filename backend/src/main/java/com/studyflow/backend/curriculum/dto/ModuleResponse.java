package com.studyflow.backend.curriculum.dto;

import com.studyflow.backend.curriculum.entity.CourseModule;

import java.time.LocalDateTime;

public record ModuleResponse(

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

    public static ModuleResponse from(CourseModule module) {

        return new ModuleResponse(
                module.getId(),
                module.getCourse().getId(),
                module.getCourse().getTitle(),
                module.getTitle(),
                module.getDescription(),
                module.getSequenceNumber(),
                module.isPublished(),
                module.getCreatedAt(),
                module.getUpdatedAt()
        );
    }
}