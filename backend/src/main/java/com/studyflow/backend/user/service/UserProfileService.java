package com.studyflow.backend.user.service;

import com.studyflow.backend.user.dto.UpdateProfileRequest;
import com.studyflow.backend.user.dto.UserProfileResponse;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.user.entity.UserProfile;
import com.studyflow.backend.user.repository.UserProfileRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(
            UserProfileRepository userProfileRepository
    ) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(AppUser user) {

        UserProfile profile = userProfileRepository
                .findByUserId(user.getId())
                .orElseGet(() -> new UserProfile(user));

        return UserProfileResponse.from(profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(
            AppUser user,
            UpdateProfileRequest request
    ) {

        UserProfile profile = userProfileRepository
                .findByUserId(user.getId())
                .orElseGet(() -> new UserProfile(user));

        profile.setEducationLevel(request.educationLevel());
        profile.setAcademicStream(request.academicStream());
        profile.setLearningGoal(request.learningGoal());
        profile.setSkillLevel(request.skillLevel());
        profile.setPreparationStage(request.preparationStage());
        profile.setPreferredLanguage(request.preferredLanguage());
        profile.setPreferredMode(request.preferredMode());

        boolean completed =
                request.educationLevel() != null
                        && request.learningGoal() != null
                        && request.skillLevel() != null
                        && request.preferredLanguage() != null
                        && request.preferredMode() != null;

        profile.setOnboardingCompleted(completed);

        UserProfile savedProfile =
                userProfileRepository.save(profile);

        return UserProfileResponse.from(savedProfile);
    }
}