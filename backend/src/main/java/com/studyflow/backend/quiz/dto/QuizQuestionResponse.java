package com.studyflow.backend.quiz.dto;

import java.util.List;

public record QuizQuestionResponse(

        Long id,
        String questionText,
        Integer sequenceNumber,
        Integer marks,
        List<OptionResponse> options
) {

    public record OptionResponse(
            Long id,
            String optionText
    ) {
    }
}