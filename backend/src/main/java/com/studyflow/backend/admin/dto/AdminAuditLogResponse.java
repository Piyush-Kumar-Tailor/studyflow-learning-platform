
package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminAuditLogResponse(
        Long id,
        Long adminId,
        String adminName,
        Long targetUserId,
        String targetUserName,
        String action,
        String oldValue,
        String newValue,
        LocalDateTime createdAt
) {
}
