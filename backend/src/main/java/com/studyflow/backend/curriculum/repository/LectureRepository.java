package com.studyflow.backend.curriculum.repository;

import com.studyflow.backend.curriculum.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureRepository
        extends JpaRepository<Lecture, Long> {

    // Get published lectures for a module in sequence order.
    List<Lecture> findByModuleIdAndPublishedTrueOrderBySequenceNumberAsc(
            Long moduleId
    );

    // Get all lectures for a module in sequence order.
    List<Lecture> findByModuleIdOrderBySequenceNumberAsc(
            Long moduleId
    );

    // Check whether a sequence number already exists in a module.
    boolean existsByModuleIdAndSequenceNumber(
            Long moduleId,
            Integer sequenceNumber
    );

    // Check whether another lecture uses the same sequence number.
    boolean existsByModuleIdAndSequenceNumberAndIdNot(
            Long moduleId,
            Integer sequenceNumber,
            Long id
    );

    // Find a published lecture by ID.
    Optional<Lecture> findByIdAndPublishedTrue(
            Long id
    );

    // Count published lectures belonging to an active course
    // and published modules.
    @Query("""
        SELECT COUNT(l)
        FROM Lecture l
        JOIN l.module m
        JOIN m.course c
        WHERE c.id = :courseId
          AND c.active = true
          AND m.published = true
          AND l.published = true
        """)
    long countPublishedLecturesByCourseId(
            @Param("courseId") Long courseId
    );

    // Retrieve published lectures for a course in curriculum order.
    @Query("""
        SELECT l
        FROM Lecture l
        JOIN l.module m
        JOIN m.course c
        WHERE c.id = :courseId
          AND c.active = true
          AND m.published = true
          AND l.published = true
        ORDER BY m.sequenceNumber ASC, l.sequenceNumber ASC
        """)
    List<Lecture> findPublishedLecturesByCourseId(
            @Param("courseId") Long courseId
    );
}