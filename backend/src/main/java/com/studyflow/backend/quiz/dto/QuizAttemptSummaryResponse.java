package com.studyflow.backend.quiz.dto;

import com.studyflow.backend.quiz.enums.QuizAttemptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuizAttemptSummaryResponse(
        Long attemptId,
        Long quizId,
        String quizTitle,
        BigDecimal score,
        BigDecimal totalMarks,
        BigDecimal percentage,
        boolean passed,
        QuizAttemptStatus status,
        LocalDateTime startedAt,
        LocalDateTime submittedAt
) {
}