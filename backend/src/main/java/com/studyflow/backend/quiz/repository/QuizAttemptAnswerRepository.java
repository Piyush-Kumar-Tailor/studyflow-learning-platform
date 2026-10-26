package com.studyflow.backend.quiz.repository;

import com.studyflow.backend.quiz.entity.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptAnswerRepository
        extends JpaRepository<QuizAttemptAnswer, Long> {

    // Retrieve all answers for an attempt.
    List<QuizAttemptAnswer> findByAttemptId(Long attemptId);

    // Retrieve answers in a consistent order.
    @Query("""
    SELECT a
    FROM QuizAttemptAnswer a
    JOIN a.question q
    WHERE a.attempt.id = :attemptId
    ORDER BY q.sequenceNumber ASC
    """)
    List<QuizAttemptAnswer> findByAttemptIdOrderByQuestionSequence(
            @Param("attemptId") Long attemptId
    );
    // Find an answer for a particular question within an attempt.
    Optional<QuizAttemptAnswer> findByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    // Check whether an answer has already been recorded.
    boolean existsByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    // Count submitted answers for an attempt.
    long countByAttemptId(Long attemptId);
}