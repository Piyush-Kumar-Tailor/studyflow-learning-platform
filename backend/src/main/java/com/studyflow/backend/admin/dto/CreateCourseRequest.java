package com.studyflow.backend.admin.dto;

import com.studyflow.backend.user.enums.SkillLevel;
import com.studyflow.backend.user.enums.LanguagePreference;

public record CreateCourseRequest(
        String title,
        String description,
        Long examId,
        String instructorName,
        SkillLevel difficulty,
        LanguagePreference language,
        String thumbnailUrl,
        String youtubePlaylistId
) {
}