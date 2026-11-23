package com.studyflow.backend.admin.controller;

import com.studyflow.backend.admin.dto.*;
import com.studyflow.backend.admin.service.AdminDashboardService;

import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import com.studyflow.backend.notification.dto.NotificationResponse;
import com.studyflow.backend.user.enums.ExamCategory;
import com.studyflow.backend.user.enums.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.studyflow.backend.admin.dto.RecentActivityResponse;
import com.studyflow.backend.admin.dto.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.studyflow.backend.admin.dto.AdminExamResponse;
import com.studyflow.backend.admin.dto.UpdateUserStatusRequest;
import com.studyflow.backend.admin.dto.UpdateUserRoleRequest;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.admin.dto.AdminAuditLogResponse;
import com.studyflow.backend.user.enums.SkillLevel;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.admin.dto.AdminCourseResponse;
import com.studyflow.backend.admin.dto.UpdateCourseStatusRequest;
import com.studyflow.backend.admin.dto.UpdateCourseRequest;

import jakarta.validation.Valid;


import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(
            AdminDashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminDashboardResponse> getDashboardStatistics() {

        AdminDashboardResponse response =
                dashboardService.getDashboardStatistics();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-students")
    public ResponseEntity<List<RecentStudentResponse>> getRecentStudents() {

        return ResponseEntity.ok(
                dashboardService.getRecentStudents()
        );
    }

    @GetMapping("/popular-courses")
    public ResponseEntity<List<PopularCourseResponse>> getPopularCourses() {

        return ResponseEntity.ok(
                dashboardService.getPopularCourses()
        );
    }

    @GetMapping("/overview")
    public ResponseEntity<AdminDashboardOverviewResponse> getDashboardOverview() {

        return ResponseEntity.ok(
                dashboardService.getDashboardOverview()
        );
    }

    @GetMapping("/registration-trend")
    public ResponseEntity<List<DailyRegistrationResponse>>
    getRegistrationTrend() {

        return ResponseEntity.ok(
                dashboardService.getDailyStudentRegistrations()
        );
    }

    @GetMapping("/enrollment-trend")
    public ResponseEntity<List<DailyEnrollmentResponse>>
    getEnrollmentTrend() {

        return ResponseEntity.ok(
                dashboardService.getDailyEnrollments()
        );
    }

    @PostMapping("/courses")
    public ResponseEntity<AdminCourseResponse> createCourse(
            @RequestBody CreateCourseRequest request
    ) {
        AdminCourseResponse response =
                dashboardService.createCourse(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/quiz-performance")
    public ResponseEntity<QuizPerformanceResponse> getQuizPerformance() {

        return ResponseEntity.ok(
                dashboardService.getQuizPerformance()
        );
    }

    @GetMapping("/course-quiz-performance")
    public ResponseEntity<List<CourseQuizPerformanceResponse>>
    getCourseQuizPerformance() {

        return ResponseEntity.ok(
                dashboardService.getCourseQuizPerformance()
        );
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivityResponse>> getRecentActivity() {
        return ResponseEntity.ok(
                dashboardService.getRecentActivity()
        );
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                dashboardService.getAllUsers(pageable)
        );
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<AdminUserResponse>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        Role selectedRole = null;

        if (role != null && !role.isBlank()) {
            try {
                selectedRole = Role.valueOf(role.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid user role"
                );
            }
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                dashboardService.searchUsers(
                        keyword,
                        selectedRole,
                        pageable
                )
        );
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request,
            @AuthenticationPrincipal AppUser currentUser
    ) {
        return ResponseEntity.ok(
                dashboardService.updateUserStatus(
                        userId,
                        request.enabled(),
                        currentUser
                )
        );
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request,
            @AuthenticationPrincipal AppUser currentUser
    ) {
        return ResponseEntity.ok(
                dashboardService.updateUserRole(
                        userId,
                        request.role(),
                        currentUser
                )
        );
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AdminAuditLogResponse>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = createAuditPageable(page, size);

        return ResponseEntity.ok(
                dashboardService.getAllAuditLogs(pageable)
        );
    }

    @GetMapping("/audit-logs/users/{userId}")
    public ResponseEntity<Page<AdminAuditLogResponse>> getAuditLogsByTargetUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = createAuditPageable(page, size);

        return ResponseEntity.ok(
                dashboardService.getAuditLogsByTargetUser(
                        userId, pageable
                )
        );
    }

    @GetMapping("/audit-logs/admins/{adminId}")
    public ResponseEntity<Page<AdminAuditLogResponse>> getAuditLogsByAdmin(
            @PathVariable Long adminId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = createAuditPageable(page, size);

        return ResponseEntity.ok(
                dashboardService.getAuditLogsByAdmin(
                        adminId, pageable
                )
        );
    }

    private Pageable createAuditPageable(int page, int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        return PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    @GetMapping("/courses/search")
    public ResponseEntity<Page<AdminCourseResponse>> searchAdminCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        SkillLevel selectedDifficulty = null;
        LanguagePreference selectedLanguage = null;

        try {
            if (difficulty != null && !difficulty.isBlank()) {
                selectedDifficulty = SkillLevel.valueOf(
                        difficulty.trim().toUpperCase()
                );
            }

            if (language != null && !language.isBlank()) {
                selectedLanguage = LanguagePreference.valueOf(
                        language.trim().toUpperCase()
                );
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid difficulty or language value"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                dashboardService.searchAdminCourses(
                        keyword,
                        selectedDifficulty,
                        selectedLanguage,
                        active,
                        pageable
                )
        );
    }

    @PatchMapping("/courses/{courseId}/status")
    public ResponseEntity<AdminCourseResponse> updateCourseStatus(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseStatusRequest request
    ) {
        return ResponseEntity.ok(
                dashboardService.updateCourseStatus(
                        courseId,
                        request.active()
                )
        );
    }


    @PutMapping("/courses/{courseId}")
    public ResponseEntity<AdminCourseResponse> updateCourse(
            @PathVariable Long courseId,
            @RequestBody UpdateCourseRequest request
    ) {
        return ResponseEntity.ok(
                dashboardService.updateCourse(
                        courseId,
                        request
                )
        );
    }



    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId
    ) {
        dashboardService.deleteCourse(courseId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/exams")
    public ResponseEntity<Page<AdminExamResponse>> getAllAdminExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                dashboardService.getAllAdminExams(pageable)
        );
    }

    @GetMapping("/exams/search")
    public ResponseEntity<Page<AdminExamResponse>> searchAdminExams(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        ExamCategory examCategory = null;

        if (category != null && !category.isBlank()) {
            try {
                examCategory = ExamCategory.valueOf(
                        category.trim().toUpperCase()
                );
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid exam category: " + category
                );
            }
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                dashboardService.searchAdminExams(
                        keyword,
                        examCategory,
                        active,
                        pageable
                )
        );
    }

    @PatchMapping("/exams/{examId}/status")
    public ResponseEntity<AdminExamResponse> updateExamStatus(
            @PathVariable Long examId,
            @RequestParam boolean active
    ) {
        return ResponseEntity.ok(
                dashboardService.updateExamStatus(examId, active)
        );
    }

    @PutMapping("/exams/{examId}")
    public ResponseEntity<AdminExamResponse> updateExam(
            @PathVariable Long examId,
            @RequestBody AdminExamUpdateRequest request
    ) {
        return ResponseEntity.ok(
                dashboardService.updateExam(examId, request)
        );
    }

    @DeleteMapping("/exams/{examId}")
    public ResponseEntity<Void> deleteExam(
            @PathVariable Long examId
    ) {
        dashboardService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exams")
    public ResponseEntity<AdminExamResponse> createExam(
            @RequestBody AdminExamCreateRequest request
    ) {
        AdminExamResponse response =
                dashboardService.createExam(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<AdminModuleResponse> createCourseModule(
            @PathVariable Long courseId,
            @RequestBody AdminModuleCreateRequest request
    ) {
        AdminModuleResponse response =
                dashboardService.createCourseModule(courseId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<AdminModuleResponse>> getCourseModules(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(
                dashboardService.getCourseModules(courseId)
        );
    }

    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<AdminModuleResponse> updateCourseModule(
            @PathVariable Long moduleId,
            @RequestBody AdminModuleUpdateRequest request
    ) {
        return ResponseEntity.ok(
                dashboardService.updateCourseModule(moduleId, request)
        );
    }

    @PatchMapping("/modules/{moduleId}/publish")
    public ResponseEntity<AdminModuleResponse> updateModulePublishStatus(
            @PathVariable Long moduleId,
            @RequestParam boolean published
    ) {
        return ResponseEntity.ok(
                dashboardService.updateModulePublishStatus(
                        moduleId,
                        published
                )
        );
    }

    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<Void> deleteCourseModule(
            @PathVariable Long moduleId
    ) {
        dashboardService.deleteCourseModule(moduleId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/modules/{moduleId}/lectures")
    public ResponseEntity<AdminLectureResponse> createLecture(
            @PathVariable Long moduleId,
            @RequestBody AdminLectureCreateRequest request
    ) {
        AdminLectureResponse response =
                dashboardService.createLecture(moduleId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/modules/{moduleId}/lectures")
    public ResponseEntity<List<AdminLectureResponse>> getModuleLectures(
            @PathVariable Long moduleId
    ) {
        return ResponseEntity.ok(
                dashboardService.getModuleLectures(moduleId)
        );
    }


    @PutMapping("/lectures/{lectureId}")
    public ResponseEntity<AdminLectureResponse> updateLecture(
            @PathVariable Long lectureId,
            @RequestBody AdminLectureUpdateRequest request
    ) {
        return ResponseEntity.ok(
                dashboardService.updateLecture(lectureId, request)
        );
    }


    @PatchMapping("/lectures/{lectureId}/publish")
    public ResponseEntity<AdminLectureResponse> updateLecturePublishStatus(
            @PathVariable Long lectureId,
            @RequestParam boolean published
    ) {
        return ResponseEntity.ok(
                dashboardService.updateLecturePublishStatus(
                        lectureId,
                        published
                )
        );
    }

    @DeleteMapping("/lectures/{lectureId}")
    public ResponseEntity<Void> deleteLecture(
            @PathVariable Long lectureId
    ) {
        dashboardService.deleteLecture(lectureId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/courses/{courseId}/modules/reorder")
    public ResponseEntity<Void> reorderCourseModules(
            @PathVariable Long courseId,
            @RequestBody AdminModuleReorderRequest request
    ) {
        dashboardService.reorderCourseModules(courseId, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/modules/{moduleId}/lectures/reorder")
    public ResponseEntity<Void> reorderModuleLectures(
            @PathVariable Long moduleId,
            @RequestBody AdminLectureReorderRequest request
    ) {
        dashboardService.reorderModuleLectures(moduleId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/enrollments")
    public ResponseEntity<Page<AdminEnrollmentResponse>> getAdminEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EnrollmentStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                dashboardService.getAdminEnrollments(pageable, status)
        );
    }

    @PatchMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<AdminEnrollmentResponse> updateEnrollmentStatus(
            @PathVariable Long enrollmentId,
            @RequestBody AdminEnrollmentStatusRequest request
    ) {
        return ResponseEntity.ok(
                dashboardService.updateEnrollmentStatus(enrollmentId, request)
        );
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<AdminEnrollmentProgressResponse> getAdminEnrollmentProgress(
            @PathVariable Long enrollmentId
    ) {
        return ResponseEntity.ok(
                dashboardService.getAdminEnrollmentProgress(enrollmentId)
        );
    }

    @GetMapping("/enrollments/{enrollmentId}/lectures/progress")
    public ResponseEntity<List<AdminLectureProgressResponse>> getDetailedLectureProgress(
            @PathVariable Long enrollmentId
    ) {
        return ResponseEntity.ok(
                dashboardService.getDetailedLectureProgress(enrollmentId)
        );
    }

    @GetMapping("/reviews")
    public ResponseEntity<Page<AdminCourseReviewResponse>> getAdminCourseReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Short rating
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination values"
            );
        }

        if (rating != null && (rating < 1 || rating > 5)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating must be between 1 and 5"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                dashboardService.getAdminCourseReviews(
                        pageable, courseId, rating
                )
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteCourseReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AppUser admin
    ) {
        dashboardService.deleteCourseReview(
                reviewId,
                admin.getId()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews/analytics")
    public ResponseEntity<AdminReviewAnalyticsResponse> getAdminReviewAnalytics() {
        return ResponseEntity.ok(
                dashboardService.getAdminReviewAnalytics()
        );
    }

    @PostMapping("/notifications/announcements")
    public ResponseEntity<Map<String, Object>> sendAnnouncementToAllStudents(
            @Valid @RequestBody AdminAnnouncementRequest request
    ) {
        int notificationsSent =
                dashboardService.sendAnnouncementToAllStudents(request);

        return ResponseEntity.ok(Map.of(
                "message", "Announcement sent successfully",
                "notificationsSent", notificationsSent
        ));
    }

    @PostMapping("/notifications/student")
    public ResponseEntity<NotificationResponse> sendNotificationToStudent(
            @Valid @RequestBody AdminStudentNotificationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                dashboardService.sendNotificationToStudent(request)
        );
    }

    @GetMapping("/notifications")
    public ResponseEntity<Page<AdminNotificationResponse>> getAdminNotificationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination values"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                dashboardService.getAdminNotificationHistory(pageable)
        );
    }

    @GetMapping("/notifications/analytics")
    public ResponseEntity<AdminNotificationAnalyticsResponse> getAdminNotificationAnalytics() {
        return ResponseEntity.ok(
                dashboardService.getAdminNotificationAnalytics()
        );
    }

    @GetMapping("/reports/platform-overview/export")
    public ResponseEntity<byte[]> exportPlatformOverviewCsv() {

        byte[] csvData = dashboardService.exportPlatformOverviewCsv();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=studyflow-platform-overview.csv"
                )
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/reports/registration-enrollment/export")
    public ResponseEntity<byte[]> exportRegistrationAndEnrollmentTrendsCsv() {

        byte[] csvData =
                dashboardService.exportRegistrationAndEnrollmentTrendsCsv();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=studyflow-registration-enrollment-trends.csv"
                )
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

}