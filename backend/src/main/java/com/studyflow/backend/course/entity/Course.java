package com.studyflow.backend.course.entity;

import com.studyflow.backend.exam.entity.Exam;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.SkillLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "exam_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_courses_exam")
    )
    private Exam exam;

    @Column(name = "instructor_name", length = 150)
    private String instructorName;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SkillLevel difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", length = 30)
    private LanguagePreference language;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "youtube_playlist_id", length = 150)
    private String youtubePlaylistId;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Course() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public SkillLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(SkillLevel difficulty) {
        this.difficulty = difficulty;
    }

    public LanguagePreference getLanguage() {
        return language;
    }

    public void setLanguage(LanguagePreference language) {
        this.language = language;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getYoutubePlaylistId() {
        return youtubePlaylistId;
    }

    public void setYoutubePlaylistId(String youtubePlaylistId) {
        this.youtubePlaylistId = youtubePlaylistId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}