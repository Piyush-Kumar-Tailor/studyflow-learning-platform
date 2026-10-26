package com.studyflow.backend.enrollment.repository;

import com.studyflow.backend.enrollment.entity.CourseEnrollment;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseEnrollmentRepository
        extends JpaRepository<CourseEnrollment, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CourseEnrollment> findByUserIdAndCourseId(
            Long userId,
            Long courseId
    );

    Optional<CourseEnrollment> findByIdAndUserId(
            Long enrollmentId,
            Long userId
    );

    Page<CourseEnrollment> findByUserIdOrderByEnrolledAtDesc(
            Long userId,
            Pageable pageable
    );

    Page<CourseEnrollment> findByUserIdAndStatusOrderByEnrolledAtDesc(
            Long userId,
            EnrollmentStatus status,
            Pageable pageable
    );

    long countByCourseId(Long courseId);

    long countByCourseIdAndStatus(
            Long courseId,
            EnrollmentStatus status
    );
}