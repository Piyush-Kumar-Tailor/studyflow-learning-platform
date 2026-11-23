
package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminExamResponse(
        Long id,
        String name,
        String code,
        String category,
        String conductingBody,
        String description,
        String officialWebsite,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
