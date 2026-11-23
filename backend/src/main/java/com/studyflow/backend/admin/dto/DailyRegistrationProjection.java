package com.studyflow.backend.admin.dto;

import java.time.LocalDate;

public interface DailyRegistrationProjection {

    LocalDate getActivityDate();

    Long getTotalRegistrations();
}