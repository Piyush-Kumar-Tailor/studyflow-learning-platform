package com.studyflow.backend.enrollment.dto;

import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateEnrollmentStatusRequest(
        @NotNull EnrollmentStatus status
) {
}