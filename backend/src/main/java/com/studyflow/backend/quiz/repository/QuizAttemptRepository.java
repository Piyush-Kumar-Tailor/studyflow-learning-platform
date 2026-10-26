package com.studyflow.backend.quiz.repository;

import com.studyflow.backend.quiz.entity.QuizAttempt;
import com.studyflow.backend.quiz.enums.QuizAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, Long> {

    // Find an attempt owned by a particular user.
    Optional<QuizAttempt> findByIdAndUserId(
            Long attemptId,
            Long userId
    );

    // Retrieve a user's attempt history for a quiz.
    Page<QuizAttempt> findByQuizIdAndUserIdOrderByStartedAtDesc(
            Long quizId,
            Long userId,
            Pageable pageable
    );

    // Retrieve all attempts belonging to a user.
    Page<QuizAttempt> findByUserIdOrderByStartedAtDesc(
            Long userId,
            Pageable pageable
    );

    // Find the user's attempts for a quiz.
    List<QuizAttempt> findByQuizIdAndUserIdOrderByStartedAtDesc(
            Long quizId,
            Long userId
    );

    // Count attempts for a quiz and user.
    long countByQuizIdAndUserId(
            Long quizId,
            Long userId
    );

    // Find attempts by status.
    List<QuizAttempt> findByStatus(
            QuizAttemptStatus status
    );

    Optional<QuizAttempt> findFirstByQuizIdAndUserIdAndStatusOrderByStartedAtDesc(
            Long quizId,
            Long userId,
            QuizAttemptStatus status
    );
}