package com.studyflow.backend.exam.dto;

import com.studyflow.backend.exam.entity.Exam;
import com.studyflow.backend.user.enums.ExamCategory;

import java.time.LocalDateTime;

public record ExamResponse(
        Long id,
        String name,
        String code,
        ExamCategory category,
        String conductingBody,
        String description,
        String officialWebsite,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ExamResponse from(Exam exam) {
        return new ExamResponse(
                exam.getId(),
                exam.getName(),
                exam.getCode(),
                exam.getCategory(),
                exam.getConductingBody(),
                exam.getDescription(),
                exam.getOfficialWebsite(),
                exam.isActive(),
                exam.getCreatedAt(),
                exam.getUpdatedAt()
        );
    }
}