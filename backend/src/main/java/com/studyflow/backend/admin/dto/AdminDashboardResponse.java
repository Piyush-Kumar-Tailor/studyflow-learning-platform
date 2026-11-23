package com.studyflow.backend.admin.dto;

public record AdminDashboardResponse(

        // User statistics
        long totalUsers,
        long totalStudents,
        long totalInstructors,
        long totalAdmins,
        long totalSuperAdmins,
        long enabledUsers,
        long activeStudents,

        // Course statistics
        long totalCourses,
        long activeCourses,

        // Enrollment statistics
        long totalEnrollments,

        // Quiz statistics
        long totalQuizzes,
        long publishedQuizzes,
        long totalQuizAttempts

) {
}