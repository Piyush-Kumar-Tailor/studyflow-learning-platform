package com.studyflow.backend.admin.dto;

public record AdminEnrollmentProgressResponse(
        Long enrollmentId,
        Long studentId,
        String studentName,
        String studentEmail,
        Long courseId,
        String courseTitle,
        String enrollmentStatus,
        long totalLectures,
        long completedLectures,
        double completionPercentage
) {
}