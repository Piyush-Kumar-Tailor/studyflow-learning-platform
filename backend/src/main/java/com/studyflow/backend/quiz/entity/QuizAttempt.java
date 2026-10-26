package com.studyflow.backend.quiz.entity;

import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.quiz.enums.QuizAttemptStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "quiz_attempts",
        indexes = {
                @Index(
                        name = "idx_quiz_attempts_quiz_user",
                        columnList = "quiz_id, user_id"
                ),
                @Index(
                        name = "idx_quiz_attempts_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_quiz_attempts_status",
                        columnList = "status"
                )
        }
)
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "quiz_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_quiz_attempts_quiz")
    )
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_quiz_attempts_user")
    )
    private AppUser user;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal score = BigDecimal.ZERO;

    @Column(name = "total_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalMarks = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean passed = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuizAttemptStatus status = QuizAttemptStatus.IN_PROGRESS;

    public QuizAttempt() {
    }

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(BigDecimal totalMarks) {
        this.totalMarks = totalMarks;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public QuizAttemptStatus getStatus() {
        return status;
    }

    public void setStatus(QuizAttemptStatus status) {
        this.status = status;
    }
}