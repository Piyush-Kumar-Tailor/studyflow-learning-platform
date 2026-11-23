package com.studyflow.backend.admin.dto;

import java.time.LocalDate;

public record DailyEnrollmentResponse(
        LocalDate date,
        long enrollments
) {
}