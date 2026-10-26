package com.studyflow.backend.enrollment.controller;

import com.studyflow.backend.enrollment.dto.CourseProgressResponse;
import com.studyflow.backend.enrollment.dto.ContinueLearningResponse;
import com.studyflow.backend.enrollment.dto.LectureProgressResponse;
import com.studyflow.backend.enrollment.dto.UpdateLectureProgressRequest;
import com.studyflow.backend.enrollment.service.LectureProgressService;
import com.studyflow.backend.user.entity.AppUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
public class LectureProgressController {

    private final LectureProgressService progressService;

    public LectureProgressController(
            LectureProgressService progressService
    ) {
        this.progressService = progressService;
    }

    @PutMapping(
            "/enrollments/{enrollmentId}/lectures/{lectureId}/progress"
    )
    public LectureProgressResponse updateProgress(
            @PathVariable Long enrollmentId,
            @PathVariable Long lectureId,
            @Valid @RequestBody UpdateLectureProgressRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        return progressService.updateProgress(
                user.getId(), enrollmentId, lectureId, request
        );
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    public CourseProgressResponse getCourseProgress(
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal AppUser user
    ) {
        return progressService.getCourseProgress(
                user.getId(), enrollmentId
        );
    }

    @GetMapping("/enrollments/{enrollmentId}/lecture-progress")
    public List<LectureProgressResponse> getLectureProgress(
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal AppUser user
    ) {
        return progressService.getLectureProgress(
                user.getId(), enrollmentId
        );
    }

    @GetMapping("/continue-learning")
    public ContinueLearningResponse getContinueLearning(
            @AuthenticationPrincipal AppUser user
    ) {
        return progressService.getContinueLearning(user.getId());
    }
}