package com.studyflow.backend.enrollment.dto;

import com.studyflow.backend.enrollment.entity.LectureProgress;
import com.studyflow.backend.enrollment.enums.LectureProgressStatus;

import java.time.LocalDateTime;

public record LectureProgressResponse(
        Long id,
        Long lectureId,
        String lectureTitle,
        Long moduleId,
        String moduleTitle,
        LectureProgressStatus status,
        Integer lastPositionSeconds,
        Integer durationSeconds,
        LocalDateTime completedAt,
        LocalDateTime updatedAt
) {
    public static LectureProgressResponse from(
            LectureProgress progress
    ) {
        return new LectureProgressResponse(
                progress.getId(),
                progress.getLecture().getId(),
                progress.getLecture().getTitle(),
                progress.getLecture().getModule().getId(),
                progress.getLecture().getModule().getTitle(),
                progress.getStatus(),
                progress.getLastPositionSeconds(),
                progress.getLecture().getDurationSeconds(),
                progress.getCompletedAt(),
                progress.getUpdatedAt()
        );
    }
}