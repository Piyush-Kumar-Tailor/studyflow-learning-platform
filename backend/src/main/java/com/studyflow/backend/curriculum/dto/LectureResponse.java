package com.studyflow.backend.curriculum.dto;

import com.studyflow.backend.curriculum.entity.Lecture;

import java.time.LocalDateTime;

public record LectureResponse(

        Long id,
        Long moduleId,
        String moduleTitle,
        Long courseId,
        String title,
        String description,
        String youtubeVideoId,
        Integer durationSeconds,
        Integer sequenceNumber,
        boolean published,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    public static LectureResponse from(Lecture lecture) {

        return new LectureResponse(
                lecture.getId(),
                lecture.getModule().getId(),
                lecture.getModule().getTitle(),
                lecture.getModule().getCourse().getId(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getYoutubeVideoId(),
                lecture.getDurationSeconds(),
                lecture.getSequenceNumber(),
                lecture.isPublished(),
                lecture.getCreatedAt(),
                lecture.getUpdatedAt()
        );
    }
}