package com.studyflow.backend.course.repository;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.SkillLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.user.enums.SkillLevel;
import com.studyflow.backend.user.enums.LanguagePreference;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository
        extends JpaRepository<Course, Long>,
        JpaSpecificationExecutor<Course> {

    Optional<Course> findBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);

    Page<Course> findByActiveTrue(Pageable pageable);

    Page<Course> findByExamIdAndActiveTrue(
            Long examId,
            Pageable pageable
    );

    Page<Course> findByTitleContainingIgnoreCaseAndActiveTrue(
            String title,
            Pageable pageable
    );

    Page<Course> findByExamIdAndTitleContainingIgnoreCaseAndActiveTrue(
            Long examId,
            String title,
            Pageable pageable
    );

    Page<Course> findByDifficultyAndActiveTrue(
            SkillLevel difficulty,
            Pageable pageable
    );

    Page<Course> findByLanguageAndActiveTrue(
            LanguagePreference language,
            Pageable pageable
    );

    Page<Course> findByExamIdAndDifficultyAndActiveTrue(
            Long examId,
            SkillLevel difficulty,
            Pageable pageable
    );

    Page<Course> findByExamIdAndLanguageAndActiveTrue(
            Long examId,
            LanguagePreference language,
            Pageable pageable
    );

    long countByActiveTrue();


    @Query("""
    SELECT c FROM Course c
    WHERE (
        :keyword IS NULL
        OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.instructorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (:difficulty IS NULL OR c.difficulty = :difficulty)
    AND (:language IS NULL OR c.language = :language)
    AND (:active IS NULL OR c.active = :active)
    """)
    Page<Course> searchAdminCourses(
            @Param("keyword") String keyword,
            @Param("difficulty") SkillLevel difficulty,
            @Param("language") LanguagePreference language,
            @Param("active") Boolean active,
            Pageable pageable
    );

    boolean existsByExamId(Long examId);
    boolean existsBySlug(String slug);

}