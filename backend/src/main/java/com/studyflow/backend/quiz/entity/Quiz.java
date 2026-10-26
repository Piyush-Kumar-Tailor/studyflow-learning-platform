package com.studyflow.backend.quiz.entity;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.curriculum.entity.Lecture;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "quizzes",
        indexes = {
                @Index(name = "idx_quizzes_course", columnList = "course_id"),
                @Index(name = "idx_quizzes_lecture", columnList = "lecture_id"),
                @Index(name = "idx_quizzes_published", columnList = "published")
        }
)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_quizzes_course")
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "lecture_id",
            foreignKey = @ForeignKey(name = "fk_quizzes_lecture")
    )
    private Lecture lecture;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 30;

    @Column(
            name = "passing_percentage",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal passingPercentage = new BigDecimal("40.00");

    @Column(nullable = false)
    private boolean published = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Quiz() {
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPassingPercentage() {
        return passingPercentage;
    }

    public void setPassingPercentage(BigDecimal passingPercentage) {
        this.passingPercentage = passingPercentage;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}