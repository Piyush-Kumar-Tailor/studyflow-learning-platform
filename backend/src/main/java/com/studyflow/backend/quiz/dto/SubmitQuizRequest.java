package com.studyflow.backend.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SubmitQuizRequest(

        @NotEmpty(message = "At least one answer is required")
        @Valid
        List<AnswerRequest> answers
) {

    public record AnswerRequest(

            @NotNull(message = "Question ID is required")
            @Positive(message = "Question ID must be positive")
            Long questionId,

            @NotNull(message = "Selected option ID is required")
            @Positive(message = "Option ID must be positive")
            Long selectedOptionId
    ) {
    }
}