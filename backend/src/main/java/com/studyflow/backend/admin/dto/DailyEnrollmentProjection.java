package com.studyflow.backend.admin.dto;

import java.time.LocalDate;

public interface DailyEnrollmentProjection {

    LocalDate getActivityDate();

    Long getTotalEnrollments();
}