package com.studyflow.backend.dashboard.dto;

import com.studyflow.backend.enrollment.dto.ContinueLearningResponse;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;

import java.util.List;

public record StudentDashboardResponse(
        long totalEnrolledCourses,
        long inProgressCourses,
        long completedCourses,
        long totalLecturesCompleted,
        List<RecentCourseProgress> recentCourses,
        ContinueLearningResponse continueLearning
) {

    public record RecentCourseProgress(
            Long enrollmentId,
            Long courseId,
            String courseTitle,
            EnrollmentStatus status,
            long totalLectures,
            long completedLectures,
            double progressPercentage
    ) {
    }
}