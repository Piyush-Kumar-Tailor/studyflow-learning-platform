package com.studyflow.backend.quiz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuizResultResponse(

        Long attemptId,
        Long quizId,
        String quizTitle,
        BigDecimal score,
        BigDecimal totalMarks,
        BigDecimal percentage,
        BigDecimal passingPercentage,
        boolean passed,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        List<AnswerResult> answers
) {

    public record AnswerResult(

            Long questionId,
            String questionText,
            Long selectedOptionId,
            String selectedOptionText,
            boolean correct,
            BigDecimal marksAwarded,
            Integer questionMarks
    ) {
    }
}