package com.studyflow.backend.enrollment.repository;

import com.studyflow.backend.admin.dto.DailyEnrollmentProjection;
import com.studyflow.backend.admin.dto.PopularCourseResponse;
import com.studyflow.backend.enrollment.entity.CourseEnrollment;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
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

    @Query("""
        SELECT new com.studyflow.backend.admin.dto.PopularCourseResponse(
            e.course.id,
            e.course.title,
            e.course.slug,
            COUNT(e.id)
        )
        FROM CourseEnrollment e
        GROUP BY e.course.id, e.course.title, e.course.slug
        ORDER BY COUNT(e.id) DESC
        """)
    List<PopularCourseResponse> findPopularCourses(Pageable pageable);

    @Query(value = """
        SELECT
            CAST(enrolled_at AS DATE) AS activity_date,
            COUNT(*) AS total_enrollments
        FROM course_enrollments
        WHERE enrolled_at >= CURRENT_DATE - INTERVAL '6 days'
          AND enrolled_at < CURRENT_DATE + INTERVAL '1 day'
        GROUP BY CAST(enrolled_at AS DATE)
        ORDER BY activity_date ASC
        """, nativeQuery = true)
    List<DailyEnrollmentProjection> findDailyEnrollments();

    List<CourseEnrollment> findAllByOrderByEnrolledAtDesc(
            Pageable pageable
    );



    // Get enrollments filtered by status for admin.
    Page<CourseEnrollment> findByStatusOrderByEnrolledAtDesc(
            EnrollmentStatus status,
            Pageable pageable
    );


}