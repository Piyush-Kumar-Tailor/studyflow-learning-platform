package com.studyflow.backend.enrollment.entity;

import com.studyflow.backend.curriculum.entity.Lecture;
import com.studyflow.backend.enrollment.enums.LectureProgressStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lecture_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_enrollment_lecture_progress",
                        columnNames = {
                                "enrollment_id",
                                "lecture_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_lecture_progress_enrollment",
                        columnList = "enrollment_id"
                ),
                @Index(
                        name = "idx_lecture_progress_lecture",
                        columnList = "lecture_id"
                ),
                @Index(
                        name = "idx_lecture_progress_status",
                        columnList = "status"
                )
        }
)
public class LectureProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Enrollment associated with this lecture progress.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "enrollment_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lecture_progress_enrollment"
            )
    )
    private CourseEnrollment enrollment;

    // Lecture for which progress is being tracked.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "lecture_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lecture_progress_lecture"
            )
    )
    private Lecture lecture;

    // Current progress status.
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private LectureProgressStatus status =
            LectureProgressStatus.NOT_STARTED;

    // Last recorded video position in seconds.
    @Min(0)
    @Column(
            name = "last_position_seconds",
            nullable = false
    )
    private Integer lastPositionSeconds = 0;

    // Time when the lecture was completed.
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Record creation timestamp.
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // Last modification timestamp.
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    // Required by JPA.
    protected LectureProgress() {
    }

    // Constructor for creating progress for a new lecture.
    public LectureProgress(
            CourseEnrollment enrollment,
            Lecture lecture
    ) {
        this.enrollment = enrollment;
        this.lecture = lecture;
        this.status = LectureProgressStatus.NOT_STARTED;
        this.lastPositionSeconds = 0;
    }

    // Initialize timestamps before inserting a record.
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    // Update timestamp before modifying a record.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters.

    public Long getId() {
        return id;
    }

    public CourseEnrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(CourseEnrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public LectureProgressStatus getStatus() {
        return status;
    }

    public void setStatus(LectureProgressStatus status) {
        this.status = status;
    }

    public Integer getLastPositionSeconds() {
        return lastPositionSeconds;
    }

    public void setLastPositionSeconds(Integer lastPositionSeconds) {
        this.lastPositionSeconds = lastPositionSeconds;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}