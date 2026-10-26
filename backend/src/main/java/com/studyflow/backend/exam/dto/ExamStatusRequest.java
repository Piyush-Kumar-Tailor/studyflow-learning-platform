package com.studyflow.backend.exam.dto;

import jakarta.validation.constraints.NotNull;

public record ExamStatusRequest(

        @NotNull(message = "Active status is required")
        Boolean active

) {
}