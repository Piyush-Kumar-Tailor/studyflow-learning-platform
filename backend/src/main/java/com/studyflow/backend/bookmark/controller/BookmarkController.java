package com.studyflow.backend.bookmark.controller;

import com.studyflow.backend.bookmark.dto.LectureBookmarkResponse;
import com.studyflow.backend.bookmark.service.BookmarkService;
import com.studyflow.backend.user.entity.AppUser;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/bookmarks")
@Validated
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // -----------------------------------------
    // 1. ADD A LECTURE BOOKMARK
    // -----------------------------------------

    @PostMapping("/lectures/{lectureId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LectureBookmarkResponse addBookmark(
            @PathVariable @Min(1) Long lectureId,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        return bookmarkService.addBookmark(
                user.getId(),
                lectureId
        );
    }

    // -----------------------------------------
    // 2. REMOVE A LECTURE BOOKMARK
    // -----------------------------------------

    @DeleteMapping("/lectures/{lectureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookmark(
            @PathVariable @Min(1) Long lectureId,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        bookmarkService.removeBookmark(
                user.getId(),
                lectureId
        );
    }

    // -----------------------------------------
    // 3. GET MY BOOKMARKS
    // -----------------------------------------

    @GetMapping
    public Page<LectureBookmarkResponse> getMyBookmarks(
            Authentication authentication,
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @RequestParam(defaultValue = "10")
            @Min(1) @Max(100) int size) {

        AppUser user = getAuthenticatedUser(authentication);

        return bookmarkService.getMyBookmarks(
                user.getId(),
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );
    }

    // -----------------------------------------
    // 4. GET AUTHENTICATED USER
    // -----------------------------------------

    private AppUser getAuthenticatedUser(
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof AppUser user)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "Authenticated user information is unavailable"
            );
        }

        return user;
    }
}