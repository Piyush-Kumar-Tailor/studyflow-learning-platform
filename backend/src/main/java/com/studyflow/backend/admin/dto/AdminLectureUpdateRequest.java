package com.studyflow.backend.admin.dto;

public record AdminLectureUpdateRequest(
        String title,
        String description,
        String youtubeVideoId,
        Integer durationSeconds,
        Integer sequenceNumber,
        Boolean published
) {
}