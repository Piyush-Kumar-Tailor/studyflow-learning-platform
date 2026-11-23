package com.studyflow.backend.exam.repository;

import com.studyflow.backend.exam.entity.Exam;
import com.studyflow.backend.user.enums.ExamCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
    SELECT e FROM Exam e
    WHERE (
        :keyword IS NULL
        OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(e.conductingBody) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (:category IS NULL OR e.category = :category)
    AND (:active IS NULL OR e.active = :active)
""")
    Page<Exam> searchAdminExams(
            @Param("keyword") String keyword,
            @Param("category") ExamCategory category,
            @Param("active") Boolean active,
            Pageable pageable
    );
}