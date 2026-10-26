package com.studyflow.backend.quiz.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StartQuizAttemptResponse(
        Long attemptId,
        Long quizId,
        String quizTitle,
        Integer durationMinutes,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        List<QuizQuestionResponse> questions
) {
}