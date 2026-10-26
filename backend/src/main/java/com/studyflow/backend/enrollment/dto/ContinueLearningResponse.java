package com.studyflow.backend.enrollment.dto;

import com.studyflow.backend.enrollment.enums.LectureProgressStatus;

public record ContinueLearningResponse(
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        Long lectureId,
        String lectureTitle,
        String youtubeVideoId,
        Integer lastPositionSeconds,
        LectureProgressStatus lectureStatus
) {
}