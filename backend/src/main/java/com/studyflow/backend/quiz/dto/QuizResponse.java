package com.studyflow.backend.quiz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuizResponse(

        Long id,
        Long courseId,
        Long lectureId,
        String title,
        String description,
        Integer durationMinutes,
        BigDecimal passingPercentage,
        boolean published,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}