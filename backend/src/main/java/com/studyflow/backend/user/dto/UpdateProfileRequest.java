package com.studyflow.backend.user.dto;

import com.studyflow.backend.user.enums.AcademicStream;
import com.studyflow.backend.user.enums.EducationLevel;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.LearningGoal;
import com.studyflow.backend.user.enums.PreferredMode;
import com.studyflow.backend.user.enums.PreparationStage;
import com.studyflow.backend.user.enums.SkillLevel;

import jakarta.validation.constraints.NotNull;

public record UpdateProfileRequest(

        @NotNull(message = "Education level is required")
        EducationLevel educationLevel,

        AcademicStream academicStream,

        @NotNull(message = "Learning goal is required")
        LearningGoal learningGoal,

        @NotNull(message = "Skill level is required")
        SkillLevel skillLevel,

        PreparationStage preparationStage,

        @NotNull(message = "Preferred language is required")
        LanguagePreference preferredLanguage,

        @NotNull(message = "Preferred learning mode is required")
        PreferredMode preferredMode

) {
}