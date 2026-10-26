package com.studyflow.backend.user.entity;

import com.studyflow.backend.user.enums.AcademicStream;
import com.studyflow.backend.user.enums.EducationLevel;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.LearningGoal;
import com.studyflow.backend.user.enums.PreferredMode;
import com.studyflow.backend.user.enums.PreparationStage;
import com.studyflow.backend.user.enums.SkillLevel;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_profile_user_id",
                        columnNames = "user_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_user_profiles_education_level",
                        columnList = "education_level"
                ),
                @Index(
                        name = "idx_user_profiles_learning_goal",
                        columnList = "learning_goal"
                )
        }
)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", length = 40)
    private EducationLevel educationLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_stream", length = 40)
    private AcademicStream academicStream;

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_goal", length = 50)
    private LearningGoal learningGoal;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", length = 30)
    private SkillLevel skillLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "preparation_stage", length = 30)
    private PreparationStage preparationStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", length = 30)
    private LanguagePreference preferredLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_mode", length = 30)
    private PreferredMode preferredMode;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UserProfile() {
        // Required by JPA.
    }

    public UserProfile(AppUser user) {
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public AcademicStream getAcademicStream() {
        return academicStream;
    }

    public void setAcademicStream(AcademicStream academicStream) {
        this.academicStream = academicStream;
    }

    public LearningGoal getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(LearningGoal learningGoal) {
        this.learningGoal = learningGoal;
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public PreparationStage getPreparationStage() {
        return preparationStage;
    }

    public void setPreparationStage(PreparationStage preparationStage) {
        this.preparationStage = preparationStage;
    }

    public LanguagePreference getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(LanguagePreference preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public PreferredMode getPreferredMode() {
        return preferredMode;
    }

    public void setPreferredMode(PreferredMode preferredMode) {
        this.preferredMode = preferredMode;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}