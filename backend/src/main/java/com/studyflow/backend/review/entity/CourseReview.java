package com.studyflow.backend.review.entity;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.user.entity.AppUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "course_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_course_reviews_course_user",
                        columnNames = {"course_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_course_reviews_course_created",
                        columnList = "course_id, created_at"
                ),
                @Index(
                        name = "idx_course_reviews_user_updated",
                        columnList = "user_id, updated_at"
                )
        }
)
public class CourseReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_course_reviews_course"
            )
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_course_reviews_user"
            )
    )
    private AppUser user;

    @Column(nullable = false)
    private Short rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CourseReview() {
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}