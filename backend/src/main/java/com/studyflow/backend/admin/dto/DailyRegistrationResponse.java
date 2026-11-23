package com.studyflow.backend.admin.dto;

import java.time.LocalDate;

public record DailyRegistrationResponse(
        LocalDate date,
        long registrations
) {
}