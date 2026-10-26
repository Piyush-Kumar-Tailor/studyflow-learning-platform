package com.studyflow.backend.bookmark.controller;

import com.studyflow.backend.bookmark.dto.CreateLectureNoteRequest;
import com.studyflow.backend.bookmark.dto.LectureNoteResponse;
import com.studyflow.backend.bookmark.dto.UpdateLectureNoteRequest;
import com.studyflow.backend.bookmark.service.LectureNoteService;
import com.studyflow.backend.user.entity.AppUser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/notes")
@Validated
public class LectureNoteController {

    private final LectureNoteService lectureNoteService;

    public LectureNoteController(LectureNoteService lectureNoteService) {
        this.lectureNoteService = lectureNoteService;
    }

    // -----------------------------------------
    // 1. CREATE A NOTE
    // -----------------------------------------

    @PostMapping("/lectures/{lectureId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LectureNoteResponse createNote(
            @PathVariable @Min(1) Long lectureId,
            @Valid @RequestBody CreateLectureNoteRequest request,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        return lectureNoteService.createNote(
                user.getId(),
                lectureId,
                request
        );
    }

    // -----------------------------------------
    // 2. GET MY NOTES
    // -----------------------------------------

    @GetMapping
    public Page<LectureNoteResponse> getMyNotes(
            Authentication authentication,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10")
            @Min(1) @Max(100) int size) {

        AppUser user = getAuthenticatedUser(authentication);

        return lectureNoteService.getMyNotes(
                user.getId(),
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "updatedAt")
                )
        );
    }

    // -----------------------------------------
    // 3. GET NOTES FOR A PARTICULAR LECTURE
    // -----------------------------------------

    @GetMapping("/lectures/{lectureId}")
    public List<LectureNoteResponse> getNotesForLecture(
            @PathVariable @Min(1) Long lectureId,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        return lectureNoteService.getNotesForLecture(
                user.getId(),
                lectureId
        );
    }

    // -----------------------------------------
    // 4. UPDATE A NOTE
    // -----------------------------------------

    @PutMapping("/{noteId}")
    public LectureNoteResponse updateNote(
            @PathVariable @Min(1) Long noteId,
            @Valid @RequestBody UpdateLectureNoteRequest request,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        return lectureNoteService.updateNote(
                user.getId(),
                noteId,
                request
        );
    }

    // -----------------------------------------
    // 5. DELETE A NOTE
    // -----------------------------------------

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(
            @PathVariable @Min(1) Long noteId,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        lectureNoteService.deleteNote(
                user.getId(),
                noteId
        );
    }

    // -----------------------------------------
    // AUTHENTICATED USER
    // -----------------------------------------

    private AppUser getAuthenticatedUser(
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof AppUser user)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "Authenticated user information is unavailable"
            );
        }

        return user;
    }
}