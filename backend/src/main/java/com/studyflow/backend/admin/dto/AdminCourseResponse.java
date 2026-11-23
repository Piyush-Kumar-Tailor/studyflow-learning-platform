
package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminCourseResponse(
        Long id,
        String title,
        String slug,
        String instructorName,
        String difficulty,
        String language,
        boolean active,
        LocalDateTime createdAt
) {
}
