package com.studyflow.backend.admin.dto;

public record PopularCourseResponse(
        Long courseId,
        String title,
        String slug,
        Long enrollmentCount
) {
}