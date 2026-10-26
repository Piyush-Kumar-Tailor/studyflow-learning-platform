package com.studyflow.backend.quiz.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "quiz_attempt_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_quiz_attempt_question",
                        columnNames = {"attempt_id", "question_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_quiz_answers_attempt",
                        columnList = "attempt_id"
                ),
                @Index(
                        name = "idx_quiz_answers_question",
                        columnList = "question_id"
                )
        }
)
public class QuizAttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "attempt_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_quiz_answers_attempt")
    )
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "question_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_quiz_answers_question")
    )
    private QuizQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "selected_option_id",
            foreignKey = @ForeignKey(name = "fk_quiz_answers_option")
    )
    private QuizOption selectedOption;

    @Column(name = "is_correct", nullable = false)
    private boolean correct = false;

    @Column(name = "marks_awarded", nullable = false, precision = 10, scale = 2)
    private BigDecimal marksAwarded = BigDecimal.ZERO;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;

    public QuizAttemptAnswer() {
    }

    @PrePersist
    protected void onCreate() {
        if (answeredAt == null) {
            answeredAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public QuizAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(QuizAttempt attempt) {
        this.attempt = attempt;
    }

    public QuizQuestion getQuestion() {
        return question;
    }

    public void setQuestion(QuizQuestion question) {
        this.question = question;
    }

    public QuizOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(QuizOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public BigDecimal getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(BigDecimal marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
}