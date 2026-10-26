package com.studyflow.backend.curriculum.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePublishedRequest(

        @NotNull(message = "Published status is required")
        Boolean published

) {
}