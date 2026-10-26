package com.studyflow.backend.user.dto;

import com.studyflow.backend.user.entity.UserProfile;
import com.studyflow.backend.user.enums.AcademicStream;
import com.studyflow.backend.user.enums.EducationLevel;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.LearningGoal;
import com.studyflow.backend.user.enums.PreferredMode;
import com.studyflow.backend.user.enums.PreparationStage;
import com.studyflow.backend.user.enums.SkillLevel;

import java.time.LocalDateTime;

public record UserProfileResponse(

        Long id,
        Long userId,
        EducationLevel educationLevel,
        AcademicStream academicStream,
        LearningGoal learningGoal,
        SkillLevel skillLevel,
        PreparationStage preparationStage,
        LanguagePreference preferredLanguage,
        PreferredMode preferredMode,
        boolean onboardingCompleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    public static UserProfileResponse from(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getEducationLevel(),
                profile.getAcademicStream(),
                profile.getLearningGoal(),
                profile.getSkillLevel(),
                profile.getPreparationStage(),
                profile.getPreferredLanguage(),
                profile.getPreferredMode(),
                profile.isOnboardingCompleted(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}