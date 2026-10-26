package com.studyflow.backend.bookmark.service;

import com.studyflow.backend.bookmark.dto.CreateLectureNoteRequest;
import com.studyflow.backend.bookmark.dto.LectureNoteResponse;
import com.studyflow.backend.bookmark.dto.UpdateLectureNoteRequest;
import com.studyflow.backend.bookmark.entity.LectureNote;
import com.studyflow.backend.bookmark.repository.LectureNoteRepository;
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
public class LectureNoteService {

    private final LectureNoteRepository noteRepository;
    private final LectureRepository lectureRepository;
    private final EntityManager entityManager;

    public LectureNoteService(
            LectureNoteRepository noteRepository,
            LectureRepository lectureRepository,
            EntityManager entityManager) {

        this.noteRepository = noteRepository;
        this.lectureRepository = lectureRepository;
        this.entityManager = entityManager;
    }

    // --------------------------------------------------
    // 1. CREATE A NOTE
    // --------------------------------------------------

    @Transactional
    public LectureNoteResponse createNote(
            Long userId,
            Long lectureId,
            CreateLectureNoteRequest request) {

        Lecture lecture = lectureRepository
                .findByIdAndPublishedTrue(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published lecture not found"));

        LectureNote note = new LectureNote();

        note.setUser(
                entityManager.getReference(AppUser.class, userId));

        note.setLecture(lecture);
        note.setTitle(request.title().trim());
        note.setContent(request.content().trim());

        LectureNote savedNote = noteRepository.save(note);

        return toResponse(savedNote);
    }

    // --------------------------------------------------
    // 2. GET MY NOTES
    // --------------------------------------------------

    public Page<LectureNoteResponse> getMyNotes(
            Long userId,
            Pageable pageable) {

        return noteRepository
                .findByUserIdOrderByUpdatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    // --------------------------------------------------
    // 3. GET MY NOTES FOR A LECTURE
    // --------------------------------------------------

    public java.util.List<LectureNoteResponse> getNotesForLecture(
            Long userId,
            Long lectureId) {

        return noteRepository
                .findByUserIdAndLectureIdOrderByUpdatedAtDesc(
                        userId,
                        lectureId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --------------------------------------------------
    // 4. UPDATE A NOTE
    // --------------------------------------------------

    @Transactional
    public LectureNoteResponse updateNote(
            Long userId,
            Long noteId,
            UpdateLectureNoteRequest request) {

        LectureNote note = noteRepository
                .findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Note not found"));

        note.setTitle(request.title().trim());
        note.setContent(request.content().trim());

        LectureNote updatedNote = noteRepository.save(note);

        return toResponse(updatedNote);
    }

    // --------------------------------------------------
    // 5. DELETE A NOTE
    // --------------------------------------------------

    @Transactional
    public void deleteNote(Long userId, Long noteId) {

        LectureNote note = noteRepository
                .findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Note not found"));

        noteRepository.delete(note);
    }

    // --------------------------------------------------
    // 6. ENTITY TO RESPONSE DTO
    // --------------------------------------------------

    private LectureNoteResponse toResponse(LectureNote note) {

        return new LectureNoteResponse(
                note.getId(),
                note.getLecture().getId(),
                note.getLecture().getTitle(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}