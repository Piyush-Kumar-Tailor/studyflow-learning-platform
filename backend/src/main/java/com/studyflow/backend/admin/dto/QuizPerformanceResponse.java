package com.studyflow.backend.admin.dto;

import java.math.BigDecimal;

public record QuizPerformanceResponse(
        long completedAttempts,
        long passedAttempts,
        long failedAttempts,
        BigDecimal averagePercentage,
        BigDecimal passRate
) {
}