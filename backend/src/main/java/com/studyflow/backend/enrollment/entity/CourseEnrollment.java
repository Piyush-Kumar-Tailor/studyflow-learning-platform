package com.studyflow.backend.enrollment.entity;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import com.studyflow.backend.user.entity.AppUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "course_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_enrollment_user_course",
                        columnNames = {"user_id", "course_id"}
                )
        },
        indexes = {
                @Index(name = "idx_enrollments_user_id", columnList = "user_id"),
                @Index(name = "idx_enrollments_course_id", columnList = "course_id"),
                @Index(name = "idx_enrollments_status", columnList = "status")
        }
)
public class CourseEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_enrollment_user")
    )
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_enrollment_course")
    )
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.IN_PROGRESS;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CourseEnrollment() {
    }

    public CourseEnrollment(AppUser user, Course course) {
        this.user = user;
        this.course = course;
        this.status = EnrollmentStatus.IN_PROGRESS;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (enrolledAt == null) {
            enrolledAt = now;
        }

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

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
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