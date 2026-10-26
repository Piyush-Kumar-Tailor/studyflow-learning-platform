package com.studyflow.backend.enrollment.controller;

import com.studyflow.backend.enrollment.dto.EnrollmentResponse;
import com.studyflow.backend.enrollment.dto.UpdateEnrollmentStatusRequest;
import com.studyflow.backend.enrollment.service.EnrollmentService;
import com.studyflow.backend.user.entity.AppUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/courses/{courseId}/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal AppUser user
    ) {
        return enrollmentService.enroll(user.getId(), courseId);
    }

    @GetMapping("/me/enrollments")
    public List<EnrollmentResponse> getMyEnrollments(
            @AuthenticationPrincipal AppUser user
    ) {
        return enrollmentService.getMyEnrollments(user.getId());
    }

    @GetMapping("/me/enrollments/{enrollmentId}")
    public EnrollmentResponse getMyEnrollment(
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal AppUser user
    ) {
        return enrollmentService.getMyEnrollment(
                user.getId(), enrollmentId
        );
    }

    @PatchMapping("/me/enrollments/{enrollmentId}/status")
    public EnrollmentResponse updateStatus(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody UpdateEnrollmentStatusRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        return enrollmentService.updateStatus(
                user.getId(), enrollmentId, request
        );
    }
}