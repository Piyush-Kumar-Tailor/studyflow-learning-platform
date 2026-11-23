package com.studyflow.backend.quiz.repository;

import com.studyflow.backend.admin.dto.CourseQuizPerformanceResponse;
import com.studyflow.backend.quiz.entity.QuizAttempt;
import com.studyflow.backend.quiz.enums.QuizAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.math.BigDecimal;

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

    boolean existsByQuizId(Long quizId);

    long countByStatus(QuizAttemptStatus status);

    long countByStatusAndPassedTrue(QuizAttemptStatus status);

    long countByStatusAndPassedFalse(QuizAttemptStatus status);

    @Query("""
        SELECT COALESCE(AVG(a.percentage), 0)
        FROM QuizAttempt a
        WHERE a.status = :status
        """)
    BigDecimal findAveragePercentageByStatus(
            @org.springframework.data.repository.query.Param("status")
            QuizAttemptStatus status
    );

    @Query("""
        SELECT new com.studyflow.backend.admin.dto.CourseQuizPerformanceResponse(
            a.quiz.course.id,
            a.quiz.course.title,
            COUNT(a.id),
            SUM(CASE WHEN a.passed = true THEN 1L ELSE 0L END),
            AVG(a.percentage)
        )
        FROM QuizAttempt a
        WHERE a.status = :status
        GROUP BY a.quiz.course.id, a.quiz.course.title
        ORDER BY COUNT(a.id) DESC
        """)
    List<CourseQuizPerformanceResponse> findCourseQuizPerformance(
            @org.springframework.data.repository.query.Param("status")
            QuizAttemptStatus status,
            Pageable pageable
    );

    List<QuizAttempt> findByStatusOrderBySubmittedAtDesc(
            QuizAttemptStatus status,
            Pageable pageable
    );
}