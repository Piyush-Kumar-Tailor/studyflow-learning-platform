package com.studyflow.backend.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateQuestionRequest(

        @NotBlank(message = "Question text is required")
        @Size(max = 10000, message = "Question text cannot exceed 10000 characters")
        String questionText,

        @NotNull(message = "Sequence number is required")
        @Min(value = 1, message = "Sequence number must be positive")
        Integer sequenceNumber,

        @NotNull(message = "Marks are required")
        @Min(value = 1, message = "Marks must be at least 1")
        Integer marks,

        @NotEmpty(message = "At least two options are required")
        @Size(min = 2, max = 6, message = "A question must have between 2 and 6 options")
        @Valid
        List<OptionRequest> options
) {

    public record OptionRequest(

            @NotBlank(message = "Option text is required")
            @Size(max = 2000, message = "Option text cannot exceed 2000 characters")
            String optionText,

            @NotNull(message = "Specify whether this option is correct")
            Boolean correct
    ) {
    }
}