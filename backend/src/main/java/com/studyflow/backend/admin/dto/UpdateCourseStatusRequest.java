package com.studyflow.backend.admin.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCourseStatusRequest(
        @NotNull Boolean active
) {
}