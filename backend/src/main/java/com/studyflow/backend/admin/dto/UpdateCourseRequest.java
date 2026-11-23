
package com.studyflow.backend.admin.dto;

import com.studyflow.backend.user.enums.SkillLevel;
import com.studyflow.backend.user.enums.LanguagePreference;

public record UpdateCourseRequest(
        String title,
        String description,
        String instructorName,
        SkillLevel difficulty,
        LanguagePreference language,
        String thumbnailUrl,
        String youtubePlaylistId
) {
}
