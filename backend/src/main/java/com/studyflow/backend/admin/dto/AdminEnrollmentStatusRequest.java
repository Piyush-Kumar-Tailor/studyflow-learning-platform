package com.studyflow.backend.admin.dto;

import com.studyflow.backend.enrollment.enums.EnrollmentStatus;

public record AdminEnrollmentStatusRequest(
        EnrollmentStatus status
) {
}