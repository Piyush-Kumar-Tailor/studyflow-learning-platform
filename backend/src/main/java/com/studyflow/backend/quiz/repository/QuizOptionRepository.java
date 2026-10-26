package com.studyflow.backend.quiz.repository;

import com.studyflow.backend.quiz.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizOptionRepository
        extends JpaRepository<QuizOption, Long> {

    // Retrieve all options for a question.
    List<QuizOption> findByQuestionId(Long questionId);

    // Retrieve options in ID order for consistent responses.
    List<QuizOption> findByQuestionIdOrderByIdAsc(Long questionId);

    // Find an option belonging to a specific question.
    Optional<QuizOption> findByIdAndQuestionId(
            Long optionId,
            Long questionId
    );

    // Count options for a question.
    long countByQuestionId(Long questionId);

    // Count correct options for a question.
    long countByQuestionIdAndCorrectTrue(Long questionId);
}