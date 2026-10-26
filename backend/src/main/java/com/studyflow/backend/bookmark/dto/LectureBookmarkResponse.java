package com.studyflow.backend.bookmark.dto;

import java.time.LocalDateTime;

public record LectureBookmarkResponse(
        Long id,
        Long lectureId,
        String lectureTitle,
        String courseTitle,
        String moduleTitle,
        String youtubeVideoId,
        LocalDateTime createdAt
) {
}