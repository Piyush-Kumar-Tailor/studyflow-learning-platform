package com.studyflow.backend.quiz.repository;

import com.studyflow.backend.quiz.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Find published quizzes for a course.
    Page<Quiz> findByCourseIdAndPublishedTrue(
            Long courseId,
            Pageable pageable
    );

    // Find all quizzes belonging to a course.
    Page<Quiz> findByCourseId(
            Long courseId,
            Pageable pageable
    );

    // Find a published quiz by ID.
    Optional<Quiz> findByIdAndPublishedTrue(Long id);

    // Check whether a quiz title already exists in a course.
    boolean existsByCourseIdAndTitleIgnoreCase(
            Long courseId,
            String title
    );
}