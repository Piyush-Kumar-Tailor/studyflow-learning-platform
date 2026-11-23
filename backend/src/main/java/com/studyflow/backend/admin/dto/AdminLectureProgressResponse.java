package com.studyflow.backend.admin.dto;

import com.studyflow.backend.enrollment.enums.LectureProgressStatus;

import java.time.LocalDateTime;

public record AdminLectureProgressResponse(
        Long lectureId,
        String lectureTitle,
        Long moduleId,
        String moduleTitle,
        Integer sequenceNumber,
        LectureProgressStatus status,
        Integer lastPositionSeconds,
        LocalDateTime completedAt,
        LocalDateTime updatedAt
) {
}