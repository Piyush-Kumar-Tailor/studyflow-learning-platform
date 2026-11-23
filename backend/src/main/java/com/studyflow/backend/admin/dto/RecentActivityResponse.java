
package com.studyflow.backend.admin.dto;

import java.time.LocalDateTime;

public record RecentActivityResponse(
        LocalDateTime occurredAt,
        String activityType,
        String description,
        Long userId,
        String userName,
        Long relatedId,
        String relatedTitle
) {
}
