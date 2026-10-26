package com.studyflow.backend.review.service;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.course.repository.CourseRepository;
import com.studyflow.backend.enrollment.repository.CourseEnrollmentRepository;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.review.dto.CourseReviewResponse;
import com.studyflow.backend.review.dto.CreateCourseReviewRequest;
import com.studyflow.backend.review.dto.UpdateCourseReviewRequest;
import com.studyflow.backend.review.entity.CourseReview;
import com.studyflow.backend.review.repository.CourseReviewRepository;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CourseReviewService {

    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EntityManager entityManager;
    private final CourseEnrollmentRepository enrollmentRepository;

    public CourseReviewService(
            CourseReviewRepository reviewRepository,
            CourseRepository courseRepository,
            CourseEnrollmentRepository enrollmentRepository,
            EntityManager entityManager
    ) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.entityManager = entityManager;
    }

    public CourseReviewResponse createReview(
            Long userId,
            Long courseId,
            CreateCourseReviewRequest request
    ) {
        validateRating(request.rating());

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        if (!course.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot review an inactive course"
            );
        }

        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You must be enrolled in this course to submit a review"
            );
        }

        if (reviewRepository.existsByCourseIdAndUserId(courseId, userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already reviewed this course"
            );
        }

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setUser(entityManager.getReference(AppUser.class, userId));
        review.setRating(request.rating());
        review.setReviewText(normalizeReviewText(request.reviewText()));

        CourseReview savedReview = reviewRepository.save(review);

        return toResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public Page<CourseReviewResponse> getCourseReviews(
            Long courseId,
            Pageable pageable
    ) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            );
        }

        return reviewRepository
                .findByCourseIdOrderByCreatedAtDesc(courseId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CourseReviewResponse> getMyReviews(
            Long userId,
            Pageable pageable
    ) {
        return reviewRepository
                .findByUserIdOrderByUpdatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    public CourseReviewResponse updateReview(
            Long userId,
            Long reviewId,
            UpdateCourseReviewRequest request
    ) {
        validateRating(request.rating());

        CourseReview review = reviewRepository
                .findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Review not found"
                ));

        review.setRating(request.rating());
        review.setReviewText(normalizeReviewText(request.reviewText()));

        return toResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long userId, Long reviewId) {
        CourseReview review = reviewRepository
                .findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Review not found"
                ));

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public CourseRatingSummary getCourseRatingSummary(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            );
        }

        long totalReviews = reviewRepository.countByCourseId(courseId);
        Long totalRating = reviewRepository.sumRatingByCourseId(courseId);

        double averageRating = totalReviews == 0
                ? 0.0
                : totalRating.doubleValue() / totalReviews;

        return new CourseRatingSummary(
                courseId,
                totalReviews,
                Math.round(averageRating * 100.0) / 100.0
        );
    }

    private void validateRating(Short rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating must be between 1 and 5"
            );
        }
    }

    private String normalizeReviewText(String reviewText) {
        if (reviewText == null || reviewText.isBlank()) {
            return null;
        }

        String normalized = reviewText.trim();

        if (normalized.length() > 5000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Review cannot exceed 5000 characters"
            );
        }

        return normalized;
    }

    private CourseReviewResponse toResponse(CourseReview review) {
        return new CourseReviewResponse(
                review.getId(),
                review.getCourse().getId(),
                review.getCourse().getTitle(),
                review.getUser().getId(),
                null,
                review.getRating(),
                review.getReviewText(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    public record CourseRatingSummary(
            Long courseId,
            long totalReviews,
            double averageRating
    ) {
    }
}