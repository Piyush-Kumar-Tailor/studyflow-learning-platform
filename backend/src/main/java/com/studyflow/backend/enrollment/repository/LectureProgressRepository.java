package com.studyflow.backend.enrollment.repository;

import com.studyflow.backend.enrollment.entity.LectureProgress;
import com.studyflow.backend.enrollment.enums.LectureProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureProgressRepository
        extends JpaRepository<LectureProgress, Long> {

    // Find progress for a specific lecture within an enrollment.
    Optional<LectureProgress> findByEnrollmentIdAndLectureId(
            Long enrollmentId,
            Long lectureId
    );

    // Retrieve all progress records for an enrollment,
    // ordered by the most recently updated.
    List<LectureProgress> findByEnrollmentIdOrderByUpdatedAtDesc(
            Long enrollmentId
    );

    // Find progress records by enrollment and status.
    List<LectureProgress> findByEnrollmentIdAndStatus(
            Long enrollmentId,
            LectureProgressStatus status
    );

    // Count progress records by enrollment and status.
    long countByEnrollmentIdAndStatus(
            Long enrollmentId,
            LectureProgressStatus status
    );

    // Find the most recently updated progress record
    // for a user and a specified status.
    Optional<LectureProgress>
    findFirstByEnrollmentUserIdAndStatusOrderByUpdatedAtDesc(
            Long userId,
            LectureProgressStatus status
    );

    // Check whether progress already exists for a lecture.
    boolean existsByEnrollmentIdAndLectureId(
            Long enrollmentId,
            Long lectureId
    );

    // Count completed progress records for published lectures
    // belonging to published modules in an active course.
    @Query("""
        SELECT COUNT(p)
        FROM LectureProgress p
        JOIN p.enrollment e
        JOIN p.lecture l
        JOIN l.module m
        JOIN m.course c
        WHERE e.id = :enrollmentId
          AND p.status = :status
          AND c.active = true
          AND m.published = true
          AND l.published = true
        """)
    long countPublishedCompletedProgress(
            @Param("enrollmentId") Long enrollmentId,
            @Param("status") LectureProgressStatus status
    );

    boolean existsByLectureId(Long lectureId);

    // Count all lecture progress records for an enrollment.
    long countByEnrollmentId(Long enrollmentId);

    // Retrieve lecture progress ordered by module and lecture sequence.
    @Query("""
    SELECT p
    FROM LectureProgress p
    JOIN FETCH p.lecture l
    JOIN FETCH l.module m
    WHERE p.enrollment.id = :enrollmentId
    ORDER BY m.sequenceNumber ASC, l.sequenceNumber ASC
    """)
    List<LectureProgress> findDetailedProgressByEnrollmentId(
            @Param("enrollmentId") Long enrollmentId
    );


}