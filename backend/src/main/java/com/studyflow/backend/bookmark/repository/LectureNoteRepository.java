package com.studyflow.backend.bookmark.repository;

import com.studyflow.backend.bookmark.entity.LectureNote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureNoteRepository
        extends JpaRepository<LectureNote, Long> {

    Page<LectureNote> findByUserIdOrderByUpdatedAtDesc(
            Long userId,
            Pageable pageable
    );

    List<LectureNote> findByUserIdAndLectureIdOrderByUpdatedAtDesc(
            Long userId,
            Long lectureId
    );

    Optional<LectureNote> findByIdAndUserId(
            Long noteId,
            Long userId
    );

    long countByUserId(Long userId);
}