package com.studyflow.backend.review.controller;

import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.review.dto.CourseRatingSummaryResponse;
import com.studyflow.backend.review.dto.CourseReviewResponse;
import com.studyflow.backend.review.dto.CreateCourseReviewRequest;
import com.studyflow.backend.review.dto.UpdateCourseReviewRequest;
import com.studyflow.backend.review.service.CourseReviewService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CourseReviewController {

    private final CourseReviewService reviewService;

    public CourseReviewController(CourseReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Submit a course review
    @PostMapping("/courses/{courseId}/reviews")
    public ResponseEntity<CourseReviewResponse> createReview(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long courseId,
            @Valid @RequestBody CreateCourseReviewRequest request
    ) {
        CourseReviewResponse response = reviewService.createReview(
                user.getId(),
                courseId,
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get paginated reviews for a course
    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<Page<CourseReviewResponse>> getCourseReviews(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                reviewService.getCourseReviews(courseId, pageable)
        );
    }

    // Get the average rating and review count
    @GetMapping("/courses/{courseId}/reviews/summary")
    public ResponseEntity<CourseRatingSummaryResponse> getRatingSummary(
            @PathVariable Long courseId
    ) {
        CourseReviewService.CourseRatingSummary summary =
                reviewService.getCourseRatingSummary(courseId);

        return ResponseEntity.ok(
                new CourseRatingSummaryResponse(
                        summary.courseId(),
                        summary.totalReviews(),
                        summary.averageRating()
                )
        );
    }

    // Get reviews submitted by the authenticated user
    @GetMapping("/me/reviews")
    public ResponseEntity<Page<CourseReviewResponse>> getMyReviews(
            @AuthenticationPrincipal AppUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        return ResponseEntity.ok(
                reviewService.getMyReviews(user.getId(), pageable)
        );
    }

    // Update the authenticated user's review
    @PutMapping("/me/reviews/{reviewId}")
    public ResponseEntity<CourseReviewResponse> updateReview(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateCourseReviewRequest request
    ) {
        return ResponseEntity.ok(
                reviewService.updateReview(
                        user.getId(),
                        reviewId,
                        request
                )
        );
    }

    // Delete the authenticated user's review
    @DeleteMapping("/me/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(user.getId(), reviewId);

        return ResponseEntity.noContent().build();
    }
}