package com.studyflow.backend.course.dto;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.SkillLevel;

import java.time.LocalDateTime;

public record CourseResponse(

        Long id,
        String title,
        String slug,
        String description,
        Long examId,
        String examName,
        String instructorName,
        SkillLevel difficulty,
        LanguagePreference language,
        String thumbnailUrl,
        String youtubePlaylistId,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    public static CourseResponse from(Course course) {

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getSlug(),
                course.getDescription(),
                course.getExam().getId(),
                course.getExam().getName(),
                course.getInstructorName(),
                course.getDifficulty(),
                course.getLanguage(),
                course.getThumbnailUrl(),
                course.getYoutubePlaylistId(),
                course.isActive(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}