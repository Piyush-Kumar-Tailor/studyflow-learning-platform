package com.studyflow.backend.bookmark.service;

import com.studyflow.backend.bookmark.dto.LectureBookmarkResponse;
import com.studyflow.backend.bookmark.entity.LectureBookmark;
import com.studyflow.backend.bookmark.repository.LectureBookmarkRepository;
import com.studyflow.backend.curriculum.entity.Lecture;
import com.studyflow.backend.curriculum.repository.LectureRepository;
import com.studyflow.backend.user.entity.AppUser;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class BookmarkService {

    private final LectureBookmarkRepository bookmarkRepository;
    private final LectureRepository lectureRepository;
    private final EntityManager entityManager;

    public BookmarkService(
            LectureBookmarkRepository bookmarkRepository,
            LectureRepository lectureRepository,
            EntityManager entityManager) {

        this.bookmarkRepository = bookmarkRepository;
        this.lectureRepository = lectureRepository;
        this.entityManager = entityManager;
    }

    // --------------------------------------------------
    // 1. ADD BOOKMARK
    // --------------------------------------------------

    @Transactional
    public LectureBookmarkResponse addBookmark(
            Long userId,
            Long lectureId) {

        Lecture lecture = lectureRepository
                .findByIdAndPublishedTrue(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published lecture not found"));

        if (bookmarkRepository.existsByUserIdAndLectureId(
                userId, lectureId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This lecture is already bookmarked");
        }

        LectureBookmark bookmark = new LectureBookmark();

        bookmark.setUser(
                entityManager.getReference(AppUser.class, userId));

        bookmark.setLecture(lecture);

        LectureBookmark savedBookmark =
                bookmarkRepository.save(bookmark);

        return toResponse(savedBookmark);
    }

    // --------------------------------------------------
    // 2. REMOVE BOOKMARK
    // --------------------------------------------------

    @Transactional
    public void removeBookmark(
            Long userId,
            Long lectureId) {

        LectureBookmark bookmark = bookmarkRepository
                .findByUserIdAndLectureId(userId, lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Bookmark not found"));

        bookmarkRepository.delete(bookmark);
    }

    // --------------------------------------------------
    // 3. GET MY BOOKMARKS
    // --------------------------------------------------

    public Page<LectureBookmarkResponse> getMyBookmarks(
            Long userId,
            Pageable pageable) {

        return bookmarkRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    // --------------------------------------------------
    // 4. MAP ENTITY TO RESPONSE DTO
    // --------------------------------------------------

    private LectureBookmarkResponse toResponse(
            LectureBookmark bookmark) {

        Lecture lecture = bookmark.getLecture();

        String moduleTitle = lecture.getModule() != null
                ? lecture.getModule().getTitle()
                : null;

        String courseTitle = null;

        if (lecture.getModule() != null
                && lecture.getModule().getCourse() != null) {

            courseTitle = lecture.getModule()
                    .getCourse()
                    .getTitle();
        }

        return new LectureBookmarkResponse(
                bookmark.getId(),
                lecture.getId(),
                lecture.getTitle(),
                courseTitle,
                moduleTitle,
                lecture.getYoutubeVideoId(),
                bookmark.getCreatedAt()
        );
    }
}