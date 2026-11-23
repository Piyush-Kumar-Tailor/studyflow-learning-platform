package com.studyflow.backend.quiz.repository;

import com.studyflow.backend.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizQuestionRepository
        extends JpaRepository<QuizQuestion, Long> {

    // Retrieve questions in display order.
    List<QuizQuestion> findByQuizIdOrderBySequenceNumberAsc(
            Long quizId
    );

    // Retrieve a specific question belonging to a quiz.
    Optional<QuizQuestion> findByIdAndQuizId(
            Long questionId,
            Long quizId
    );

    // Check whether a question sequence number already exists.
    boolean existsByQuizIdAndSequenceNumber(
            Long quizId,
            Integer sequenceNumber
    );

    // Count questions belonging to a quiz.
    long countByQuizId(Long quizId);

    boolean existsByQuizIdAndSequenceNumberAndIdNot(
            Long quizId,
            Integer sequenceNumber,
            Long questionId
    );
}