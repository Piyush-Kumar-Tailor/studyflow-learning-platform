package com.studyflow.backend.bookmark.entity;

import com.studyflow.backend.curriculum.entity.Lecture;
import com.studyflow.backend.user.entity.AppUser;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lecture_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_lecture_bookmarks_user_lecture",
                        columnNames = {"user_id", "lecture_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_lecture_bookmarks_user_created",
                        columnList = "user_id, created_at"
                )
        }
)
public class LectureBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public LectureBookmark() {
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

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}