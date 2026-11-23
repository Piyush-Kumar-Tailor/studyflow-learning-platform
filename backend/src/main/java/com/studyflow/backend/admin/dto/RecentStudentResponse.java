package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record RecentStudentResponse(
        Long id,
        String name,
        String email,
        LocalDateTime registeredAt
) {
}