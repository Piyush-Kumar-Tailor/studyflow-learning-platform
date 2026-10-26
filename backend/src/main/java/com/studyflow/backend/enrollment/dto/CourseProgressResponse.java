package com.studyflow.backend.enrollment.dto;

import com.studyflow.backend.enrollment.enums.EnrollmentStatus;

public record CourseProgressResponse(
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        EnrollmentStatus enrollmentStatus,
        long totalLectures,
        long completedLectures,
        double progressPercentage
) {
}