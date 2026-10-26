package com.studyflow.backend.review.repository;

import com.studyflow.backend.review.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseReviewRepository
        extends JpaRepository<CourseReview, Long> {

    // Check whether the user has already reviewed this course
    boolean existsByCourseIdAndUserId(Long courseId, Long userId);

    // Find a specific user's review for a course
    Optional<CourseReview> findByCourseIdAndUserId(
            Long courseId,
            Long userId
    );

    // Find a review only if it belongs to the specified user
    Optional<CourseReview> findByIdAndUserId(
            Long reviewId,
            Long userId
    );

    // Get reviews for a course, newest first
    Page<CourseReview> findByCourseIdOrderByCreatedAtDesc(
            Long courseId,
            Pageable pageable
    );

    // Get all reviews written by a user
    Page<CourseReview> findByUserIdOrderByUpdatedAtDesc(
            Long userId,
            Pageable pageable
    );

    // Calculate the number of reviews for a course
    long countByCourseId(Long courseId);

    // Calculate the total rating points for a course
    @Query("""
       SELECT SUM(r.rating)
       FROM CourseReview r
       WHERE r.course.id = :courseId
       """)
    Long sumRatingByCourseId(@Param("courseId") Long courseId);
}