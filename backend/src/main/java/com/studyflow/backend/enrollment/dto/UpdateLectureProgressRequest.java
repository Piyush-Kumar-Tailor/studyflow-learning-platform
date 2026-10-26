package com.studyflow.backend.enrollment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateLectureProgressRequest(

        @NotNull
        @Min(0)
        Integer lastPositionSeconds,

        @NotNull
        Boolean completed
) {
}