package com.studyflow.backend.exam.repository;

import com.studyflow.backend.exam.entity.Exam;
import com.studyflow.backend.user.enums.ExamCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    Optional<Exam> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    Page<Exam> findByActiveTrue(Pageable pageable);

    Page<Exam> findByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable
    );

    Page<Exam> findByCategoryAndActiveTrue(
            ExamCategory category,
            Pageable pageable
    );

    Page<Exam> findByCategoryAndNameContainingIgnoreCaseAndActiveTrue(
            ExamCategory category,
            String name,
            Pageable pageable
    );

    Optional<Exam> findByIdAndActiveTrue(Long id);
}