package com.studyflow.backend.bookmark.repository;

import com.studyflow.backend.bookmark.entity.LectureBookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureBookmarkRepository
        extends JpaRepository<LectureBookmark, Long> {

    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);

    Optional<LectureBookmark> findByUserIdAndLectureId(
            Long userId,
            Long lectureId
    );

    Page<LectureBookmark> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    void deleteByUserIdAndLectureId(Long userId, Long lectureId);

    long countByUserId(Long userId);

    boolean existsByLectureId(Long lectureId);
}