package com.studyflow.backend.curriculum.repository;

import com.studyflow.backend.curriculum.entity.CourseModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseModuleRepository
        extends JpaRepository<CourseModule, Long> {

    // Retrieve published modules for a course in curriculum order
    List<CourseModule> findByCourseIdAndPublishedTrueOrderBySequenceNumberAsc(
            Long courseId
    );

    // Retrieve all modules for an administrator
    List<CourseModule> findByCourseIdOrderBySequenceNumberAsc(
            Long courseId
    );

    // Retrieve published modules with pagination
    Page<CourseModule> findByCourseIdAndPublishedTrue(
            Long courseId,
            Pageable pageable
    );

    // Check whether a sequence number is already used within a course
    boolean existsByCourseIdAndSequenceNumber(
            Long courseId,
            Integer sequenceNumber
    );

    // Check whether another module uses a sequence number
    boolean existsByCourseIdAndSequenceNumberAndIdNot(
            Long courseId,
            Integer sequenceNumber,
            Long id
    );

    Optional<CourseModule> findByIdAndPublishedTrue(Long id);
}