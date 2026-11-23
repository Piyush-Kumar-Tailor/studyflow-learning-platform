package com.studyflow.backend.admin.dto;

public record CourseQuizPerformanceResponse(
        Long courseId,
        String courseTitle,
        Long completedAttempts,
        Long passedAttempts,
        Double averagePercentage
) {
}