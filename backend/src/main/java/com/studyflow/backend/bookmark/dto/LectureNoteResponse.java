package com.studyflow.backend.bookmark.dto;

import java.time.LocalDateTime;

public record LectureNoteResponse(

        Long id,
        Long lectureId,
        String lectureTitle,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}