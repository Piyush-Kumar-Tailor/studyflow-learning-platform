package com.studyflow.backend.admin.service;

import com.studyflow.backend.admin.dto.*;
import com.studyflow.backend.bookmark.repository.LectureBookmarkRepository;
import com.studyflow.backend.bookmark.repository.LectureNoteRepository;
import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.course.repository.CourseRepository;
import com.studyflow.backend.curriculum.entity.CourseModule;
import com.studyflow.backend.curriculum.entity.Lecture;
import com.studyflow.backend.curriculum.repository.CourseModuleRepository;
import com.studyflow.backend.curriculum.repository.LectureRepository;
import com.studyflow.backend.enrollment.entity.CourseEnrollment;
import com.studyflow.backend.enrollment.entity.LectureProgress;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import com.studyflow.backend.enrollment.enums.LectureProgressStatus;
import com.studyflow.backend.enrollment.repository.CourseEnrollmentRepository;
import com.studyflow.backend.enrollment.repository.LectureProgressRepository;
import com.studyflow.backend.exam.repository.ExamRepository;
import com.studyflow.backend.notification.dto.NotificationResponse;
import com.studyflow.backend.notification.repository.NotificationRepository;
import com.studyflow.backend.quiz.entity.Quiz;
import com.studyflow.backend.quiz.repository.QuizAttemptRepository;
import com.studyflow.backend.quiz.repository.QuizRepository;
import com.studyflow.backend.review.entity.CourseReview;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.user.enums.ExamCategory;
import com.studyflow.backend.user.enums.Role;
import com.studyflow.backend.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import com.studyflow.backend.quiz.enums.QuizAttemptStatus;
import com.studyflow.backend.admin.dto.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.studyflow.backend.admin.repository.AdminAuditLogRepository;
import com.studyflow.backend.admin.entity.AdminAuditLog;
import com.studyflow.backend.user.enums.SkillLevel;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.admin.dto.AdminCourseResponse;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.studyflow.backend.admin.dto.UpdateCourseRequest;
import com.studyflow.backend.admin.dto.AdminExamResponse;
import com.studyflow.backend.exam.entity.Exam;
import com.studyflow.backend.review.repository.CourseReviewRepository;
import com.studyflow.backend.admin.dto.AdminAnnouncementRequest;
import com.studyflow.backend.notification.service.NotificationService;
import com.studyflow.backend.user.enums.Role;
import java.nio.charset.StandardCharsets;
import com.studyflow.backend.admin.dto.*;


import com.studyflow.backend.admin.dto.AdminAuditLogResponse;


import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AdminAuditLogRepository adminAuditLogRepository;
    private final ExamRepository examRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LectureRepository lectureRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final LectureBookmarkRepository lectureBookmarkRepository;
    private final LectureNoteRepository lectureNoteRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            CourseEnrollmentRepository enrollmentRepository,
            QuizRepository quizRepository,
            QuizAttemptRepository quizAttemptRepository,
            AdminAuditLogRepository adminAuditLogRepository,
            ExamRepository examRepository,
            CourseModuleRepository courseModuleRepository,
            LectureRepository lectureRepository,
            LectureProgressRepository lectureProgressRepository,
            LectureBookmarkRepository lectureBookmarkRepository,
            LectureNoteRepository lectureNoteRepository,
            CourseReviewRepository courseReviewRepository,
            NotificationService notificationService,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.adminAuditLogRepository = adminAuditLogRepository;
        this.examRepository = examRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lectureRepository = lectureRepository;
        this.lectureBookmarkRepository = lectureBookmarkRepository;
        this.lectureProgressRepository = lectureProgressRepository;
        this.lectureNoteRepository = lectureNoteRepository;
        this.courseReviewRepository = courseReviewRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    public Page<AdminExamResponse> searchAdminExams(
            String keyword,
            ExamCategory category,
            Boolean active,
            Pageable pageable
    ) {
        String searchKeyword = (keyword == null || keyword.isBlank())
                ? null
                : keyword.trim();

        return examRepository.searchAdminExams(
                searchKeyword,
                category,
                active,
                pageable
        ).map(exam -> new AdminExamResponse(
                exam.getId(),
                exam.getName(),
                exam.getCode(),
                exam.getCategory().name(),
                exam.getConductingBody(),
                exam.getDescription(),
                exam.getOfficialWebsite(),
                exam.isActive(),
                exam.getCreatedAt(),
                exam.getUpdatedAt()
        ));
    }

    public AdminDashboardResponse getDashboardStatistics() {

        return new AdminDashboardResponse(

                // User statistics
                userRepository.count(),
                userRepository.countByRole(Role.STUDENT),
                userRepository.countByRole(Role.INSTRUCTOR),
                userRepository.countByRole(Role.ADMIN),
                userRepository.countByRole(Role.SUPER_ADMIN),
                userRepository.countByEnabledTrue(),
                userRepository.countByRoleAndEnabledTrue(Role.STUDENT),

                // Course statistics
                courseRepository.count(),
                courseRepository.countByActiveTrue(),

                // Enrollment statistics
                enrollmentRepository.count(),

                // Quiz statistics
                quizRepository.count(),
                quizRepository.countByPublishedTrue(),
                quizAttemptRepository.count()
        );
    }

    @Transactional(readOnly = true)
    public AdminNotificationAnalyticsResponse getAdminNotificationAnalytics() {

        long total = notificationRepository.count();
        long read = notificationRepository.countByReadTrue();
        long unread = notificationRepository.countByReadFalse();

        double readPercentage = total == 0
                ? 0.0
                : Math.round((read * 10000.0) / total) / 100.0;

        Map<String, Long> typeDistribution = new LinkedHashMap<>();

        for (Object[] row : notificationRepository.countNotificationsByType()) {
            String type = (String) row[0];
            Long count = (Long) row[1];

            typeDistribution.put(type, count);
        }

        return new AdminNotificationAnalyticsResponse(
                total,
                read,
                unread,
                readPercentage,
                typeDistribution
        );
    }

    @Transactional
    public AdminEnrollmentResponse updateEnrollmentStatus(
            Long enrollmentId,
            AdminEnrollmentStatusRequest request
    ) {
        CourseEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Enrollment not found with id: " + enrollmentId
                ));

        if (request == null || request.status() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enrollment status is required"
            );
        }

        EnrollmentStatus newStatus = request.status();

        enrollment.setStatus(newStatus);

        if (newStatus == EnrollmentStatus.COMPLETED) {
            if (enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(LocalDateTime.now());
            }
        } else {
            enrollment.setCompletedAt(null);
        }

        CourseEnrollment updatedEnrollment =
                enrollmentRepository.save(enrollment);

        return new AdminEnrollmentResponse(
                updatedEnrollment.getId(),
                updatedEnrollment.getUser().getId(),
                updatedEnrollment.getCourse().getId(),
                updatedEnrollment.getCourse().getTitle(),
                updatedEnrollment.getStatus(),
                updatedEnrollment.getEnrolledAt(),
                updatedEnrollment.getCompletedAt()
        );
    }

    @Transactional(readOnly = true)
    public AdminEnrollmentProgressResponse getAdminEnrollmentProgress(
            Long enrollmentId
    ) {
        CourseEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Enrollment not found with id: " + enrollmentId
                ));

        Long courseId = enrollment.getCourse().getId();

        // Count published lectures in the course.
        long totalLectures =
                lectureRepository.countPublishedLecturesByCourseId(courseId);

        // Count completed progress records for published lectures.
        long completedLectures =
                lectureProgressRepository.countPublishedCompletedProgress(
                        enrollmentId,
                        LectureProgressStatus.COMPLETED
                );

        double completionPercentage = totalLectures == 0
                ? 0.0
                : Math.round(
                (completedLectures * 10000.0) / totalLectures
        ) / 100.0;

        return new AdminEnrollmentProgressResponse(
                enrollment.getId(),
                enrollment.getUser().getId(),
                enrollment.getUser().getName(),
                enrollment.getUser().getEmail(),
                courseId,
                enrollment.getCourse().getTitle(),
                enrollment.getStatus().name(),
                totalLectures,
                completedLectures,
                completionPercentage
        );
    }

    @Transactional(readOnly = true)
    public List<AdminLectureProgressResponse> getDetailedLectureProgress(
            Long enrollmentId
    ) {
        // Verify that the enrollment exists.
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Enrollment not found with id: " + enrollmentId
            );
        }

        List<LectureProgress> progressRecords =
                lectureProgressRepository.findDetailedProgressByEnrollmentId(
                        enrollmentId
                );

        return progressRecords.stream()
                .map(progress -> new AdminLectureProgressResponse(
                        progress.getLecture().getId(),
                        progress.getLecture().getTitle(),
                        progress.getLecture().getModule().getId(),
                        progress.getLecture().getModule().getTitle(),
                        progress.getLecture().getSequenceNumber(),
                        progress.getStatus(),
                        progress.getLastPositionSeconds(),
                        progress.getCompletedAt(),
                        progress.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AdminCourseReviewResponse> getAdminCourseReviews(
            Pageable pageable,
            Long courseId,
            Short rating
    ) {
        Page<CourseReview> reviews;

        if (courseId != null && rating != null) {
            reviews = courseReviewRepository
                    .findByCourseIdAndRatingOrderByCreatedAtDesc(
                            courseId, rating, pageable
                    );
        } else if (courseId != null) {
            reviews = courseReviewRepository
                    .findByCourseIdOrderByCreatedAtDesc(
                            courseId, pageable
                    );
        } else if (rating != null) {
            reviews = courseReviewRepository
                    .findByRatingOrderByCreatedAtDesc(
                            rating, pageable
                    );
        } else {
            reviews = courseReviewRepository.findAll(
                    PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by(Sort.Direction.DESC, "createdAt")
                    )
            );
        }

        return reviews.map(review -> new AdminCourseReviewResponse(
                review.getId(),
                review.getCourse().getId(),
                review.getCourse().getTitle(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getUser().getEmail(),
                review.getRating(),
                review.getReviewText(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        ));
    }

    @Transactional
    public void deleteCourseReview(Long reviewId, Long adminId) {

        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course review not found with id: " + reviewId
                ));

        AppUser admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Admin not found with id: " + adminId
                ));

        String oldValue = "reviewId=" + review.getId()
                + ", courseId=" + review.getCourse().getId()
                + ", studentId=" + review.getUser().getId()
                + ", rating=" + review.getRating();

        AdminAuditLog auditLog = new AdminAuditLog(
                admin,
                review.getUser(),
                "COURSE_REVIEW_DELETED",
                oldValue,
                "Review deleted by admin"
        );

        adminAuditLogRepository.save(auditLog);

        courseReviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public AdminReviewAnalyticsResponse getAdminReviewAnalytics() {

        long totalReviews = courseReviewRepository.count();

        Double averageRating = courseReviewRepository.findAverageRating();

        Map<Short, Long> ratingDistribution = new LinkedHashMap<>();

        for (short rating = 1; rating <= 5; rating++) {
            ratingDistribution.put(
                    rating,
                    courseReviewRepository.countByRating(rating)
            );
        }

        return new AdminReviewAnalyticsResponse(
                totalReviews,
                averageRating == null ? 0.0 :
                        Math.round(averageRating * 100.0) / 100.0,
                ratingDistribution
        );
    }

    public List<RecentStudentResponse> getRecentStudents() {

        return userRepository
                .findByRoleOrderByCreatedAtDesc(
                        Role.STUDENT,
                        PageRequest.of(0, 5)
                )
                .stream()
                .map(user -> new RecentStudentResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public List<PopularCourseResponse> getPopularCourses() {

        return enrollmentRepository.findPopularCourses(
                PageRequest.of(0, 5)
        );
    }

    public AdminDashboardOverviewResponse getDashboardOverview() {

        return new AdminDashboardOverviewResponse(
                getDashboardStatistics(),
                getRecentStudents(),
                getPopularCourses()
        );
    }

    public List<DailyRegistrationResponse> getDailyStudentRegistrations() {

        Map<LocalDate, Long> registrationMap =
                userRepository.findDailyStudentRegistrations()
                        .stream()
                        .collect(Collectors.toMap(
                                DailyRegistrationProjection::getActivityDate,
                                DailyRegistrationProjection::getTotalRegistrations
                        ));

        LocalDate today = LocalDate.now();

        return IntStream.rangeClosed(0, 6)
                .mapToObj(daysAgo -> {
                    LocalDate date = today.minusDays(6 - daysAgo);

                    return new DailyRegistrationResponse(
                            date,
                            registrationMap.getOrDefault(date, 0L)
                    );
                })
                .toList();
    }

    public List<DailyEnrollmentResponse> getDailyEnrollments() {

        Map<LocalDate, Long> enrollmentMap =
                enrollmentRepository.findDailyEnrollments()
                        .stream()
                        .collect(Collectors.toMap(
                                DailyEnrollmentProjection::getActivityDate,
                                DailyEnrollmentProjection::getTotalEnrollments
                        ));

        LocalDate today = LocalDate.now();

        return IntStream.rangeClosed(0, 6)
                .mapToObj(daysAgo -> {
                    LocalDate date = today.minusDays(6 - daysAgo);

                    return new DailyEnrollmentResponse(
                            date,
                            enrollmentMap.getOrDefault(date, 0L)
                    );
                })
                .toList();
    }

    public QuizPerformanceResponse getQuizPerformance() {

        QuizAttemptStatus submitted = QuizAttemptStatus.SUBMITTED;

        long completed =
                quizAttemptRepository.countByStatus(submitted);

        long passed =
                quizAttemptRepository.countByStatusAndPassedTrue(submitted);

        long failed =
                quizAttemptRepository.countByStatusAndPassedFalse(submitted);

        BigDecimal averagePercentage =
                quizAttemptRepository.findAveragePercentageByStatus(submitted);

        BigDecimal passRate = completed == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(passed)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(completed),
                        2,
                        RoundingMode.HALF_UP
                );

        return new QuizPerformanceResponse(
                completed,
                passed,
                failed,
                averagePercentage.setScale(2, RoundingMode.HALF_UP),
                passRate
        );
    }

    public List<CourseQuizPerformanceResponse> getCourseQuizPerformance() {

        return quizAttemptRepository.findCourseQuizPerformance(
                QuizAttemptStatus.SUBMITTED,
                PageRequest.of(0, 10)
        );
    }

    public List<RecentActivityResponse> getRecentActivity() {

        Pageable pageable = PageRequest.of(0, 5);

        List<RecentActivityResponse> activities = new ArrayList<>();

        // Recent student registrations
        userRepository.findByRoleOrderByCreatedAtDesc(
                Role.STUDENT, pageable
        ).forEach(user -> activities.add(
                new RecentActivityResponse(
                        user.getCreatedAt(),
                        "STUDENT_REGISTERED",
                        user.getName() + " registered on StudyFlow",
                        user.getId(),
                        user.getName(),
                        null,
                        null
                )
        ));

        // Recent course enrollments
        enrollmentRepository.findAllByOrderByEnrolledAtDesc(
                pageable
        ).forEach(enrollment -> {
            AppUser user = enrollment.getUser();
            Course course = enrollment.getCourse();

            activities.add(
                    new RecentActivityResponse(
                            enrollment.getEnrolledAt(),
                            "COURSE_ENROLLED",
                            user.getName() + " enrolled in " + course.getTitle(),
                            user.getId(),
                            user.getName(),
                            course.getId(),
                            course.getTitle()
                    )
            );
        });

        // Recent submitted quiz attempts
        quizAttemptRepository.findByStatusOrderBySubmittedAtDesc(
                QuizAttemptStatus.SUBMITTED,
                pageable
        ).forEach(attempt -> {
            AppUser user = attempt.getUser();
            Quiz quiz = attempt.getQuiz();

            activities.add(
                    new RecentActivityResponse(
                            attempt.getSubmittedAt(),
                            "QUIZ_SUBMITTED",
                            user.getName() + " submitted quiz: " + quiz.getTitle(),
                            user.getId(),
                            user.getName(),
                            quiz.getId(),
                            quiz.getTitle()
                    )
            );
        });

        return activities.stream()
                .sorted(Comparator.comparing(
                        RecentActivityResponse::occurredAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(15)
                .toList();
    }

    @Transactional
    public void reorderCourseModules(
            Long courseId,
            AdminModuleReorderRequest request
    ) {
        // Validate course
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found with ID: " + courseId
                ));

        // Validate request
        if (request.moduleIds() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Module IDs are required"
            );
        }

        List<CourseModule> modules =
                courseModuleRepository.findByCourseIdOrderBySequenceNumberAsc(
                        courseId
                );

        List<Long> requestedIds = request.moduleIds();

        // Prevent duplicate IDs or missing/foreign modules
        Set<Long> requestedIdSet = new HashSet<>(requestedIds);

        Set<Long> existingIdSet = modules.stream()
                .map(CourseModule::getId)
                .collect(Collectors.toSet());

        if (requestedIds.size() != modules.size()
                || requestedIdSet.size() != requestedIds.size()
                || !requestedIdSet.equals(existingIdSet)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide every module belonging to this course exactly once"
            );
        }

        if (modules.isEmpty()) {
            return;
        }

        // Move existing sequence numbers temporarily above the current maximum.
        // This avoids unique-constraint conflicts during reordering.
        int maxSequence = modules.stream()
                .mapToInt(CourseModule::getSequenceNumber)
                .max()
                .orElse(0);

        if ((long) maxSequence + modules.size() + 1 > Integer.MAX_VALUE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Module sequence numbers exceed the supported range"
            );
        }

        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).setSequenceNumber(maxSequence + i + 1);
        }

        courseModuleRepository.saveAllAndFlush(modules);

        // Apply the requested order using sequence numbers starting at 1.
        Map<Long, CourseModule> moduleMap = modules.stream()
                .collect(Collectors.toMap(
                        CourseModule::getId,
                        Function.identity()
                ));

        List<CourseModule> reorderedModules = new ArrayList<>();

        for (int i = 0; i < requestedIds.size(); i++) {
            CourseModule module = moduleMap.get(requestedIds.get(i));
            module.setSequenceNumber(i + 1);
            reorderedModules.add(module);
        }

        courseModuleRepository.saveAllAndFlush(reorderedModules);
    }

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.isEnabled(),
                        user.getCreatedAt()
                ));
    }

    public Page<AdminUserResponse> searchUsers(
            String keyword,
            Role role,
            Pageable pageable
    ) {
        String normalizedKeyword =
                (keyword == null || keyword.isBlank())
                        ? null
                        : keyword.trim();

        return userRepository.searchUsers(
                normalizedKeyword,
                role,
                pageable
        ).map(user -> new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isEnabled(),
                user.getCreatedAt()
        ));
    }

    @Transactional
    public void reorderModuleLectures(
            Long moduleId,
            AdminLectureReorderRequest request
    ) {
        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Module not found with id: " + moduleId
                ));

        List<Lecture> lectures =
                lectureRepository.findByModuleIdOrderBySequenceNumberAsc(moduleId);

        List<Long> requestedIds = request.lectureIds();

        if (requestedIds == null || requestedIds.size() != lectures.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide every lecture ID in the module exactly once"
            );
        }

        Set<Long> existingIds = lectures.stream()
                .map(Lecture::getId)
                .collect(Collectors.toSet());

        Set<Long> requestedIdSet = new HashSet<>(requestedIds);

        if (requestedIdSet.size() != requestedIds.size()
                || !existingIds.equals(requestedIdSet)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Lecture IDs must include every lecture in this module exactly once"
            );
        }

        int maxSequence = lectures.stream()
                .mapToInt(Lecture::getSequenceNumber)
                .max()
                .orElse(0);

        if ((long) maxSequence + lectures.size() + 1 > Integer.MAX_VALUE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to reorder lectures because sequence numbers are exhausted"
            );
        }

        // First move lectures to temporary sequence numbers
        for (int i = 0; i < lectures.size(); i++) {
            lectures.get(i).setSequenceNumber(maxSequence + i + 1);
        }

        lectureRepository.saveAllAndFlush(lectures);

        // Then apply the requested order
        Map<Long, Lecture> lectureMap = lectures.stream()
                .collect(Collectors.toMap(Lecture::getId, Function.identity()));

        List<Lecture> reorderedLectures = new ArrayList<>();

        for (int i = 0; i < requestedIds.size(); i++) {
            Lecture lecture = lectureMap.get(requestedIds.get(i));
            lecture.setSequenceNumber(i + 1);
            reorderedLectures.add(lecture);
        }

        lectureRepository.saveAllAndFlush(reorderedLectures);
    }

    @Transactional
    public AdminUserResponse updateUserStatus(
            Long userId,
            Boolean enabled,
            AppUser currentUser
    ) {
        if (currentUser.getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot change your own account status"
            );
        }

        AppUser targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // An ordinary admin cannot modify a super admin.
        if (currentUser.getRole() == Role.ADMIN
                && targetUser.getRole() == Role.SUPER_ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot modify a super admin account"
            );
        }

        // Prevent disabling the last enabled super admin.
        if (!enabled
                && targetUser.getRole() == Role.SUPER_ADMIN
                && userRepository.countByRoleAndEnabledTrue(
                Role.SUPER_ADMIN
        ) <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot disable the last enabled super admin"
            );
        }

        boolean oldEnabled = targetUser.isEnabled();

        targetUser.setEnabled(enabled);

        AppUser savedUser = userRepository.save(targetUser);

        adminAuditLogRepository.save(
                new AdminAuditLog(
                        currentUser,
                        savedUser,
                        "USER_STATUS_UPDATED",
                        String.valueOf(oldEnabled),
                        String.valueOf(enabled)
                )
        );

        return toAdminUserResponse(savedUser);
    }

    public Page<AdminAuditLogResponse> getAllAuditLogs(
            Pageable pageable
    ) {
        return adminAuditLogRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toAuditLogResponse);
    }

    public Page<AdminAuditLogResponse> getAuditLogsByTargetUser(
            Long userId,
            Pageable pageable
    ) {
        return adminAuditLogRepository
                .findByTargetUserIdOrderByCreatedAtDesc(
                        userId, pageable
                )
                .map(this::toAuditLogResponse);
    }

    public Page<AdminAuditLogResponse> getAuditLogsByAdmin(
            Long adminId,
            Pageable pageable
    ) {
        return adminAuditLogRepository
                .findByAdminIdOrderByCreatedAtDesc(
                        adminId, pageable
                )
                .map(this::toAuditLogResponse);
    }

    public Page<AdminCourseResponse> searchAdminCourses(
            String keyword,
            SkillLevel difficulty,
            LanguagePreference language,
            Boolean active,
            Pageable pageable
    ) {
        String normalizedKeyword =
                (keyword == null || keyword.isBlank())
                        ? null
                        : keyword.trim();

        return courseRepository.searchAdminCourses(
                normalizedKeyword,
                difficulty,
                language,
                active,
                pageable
        ).map(course -> new AdminCourseResponse(
                course.getId(),
                course.getTitle(),
                course.getSlug(),
                course.getInstructorName(),
                course.getDifficulty() != null
                        ? course.getDifficulty().name() : null,
                course.getLanguage() != null
                        ? course.getLanguage().name() : null,
                course.isActive(),
                course.getCreatedAt()
        ));
    }

    @Transactional
    public AdminExamResponse updateExamStatus(Long examId, boolean active) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found with ID: " + examId
                ));

        exam.setActive(active);

        Exam updatedExam = examRepository.save(exam);

        return new AdminExamResponse(
                updatedExam.getId(),
                updatedExam.getName(),
                updatedExam.getCode(),
                updatedExam.getCategory().name(),
                updatedExam.getConductingBody(),
                updatedExam.getDescription(),
                updatedExam.getOfficialWebsite(),
                updatedExam.isActive(),
                updatedExam.getCreatedAt(),
                updatedExam.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<AdminModuleResponse> getCourseModules(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found with ID: " + courseId
                ));

        return courseModuleRepository
                .findByCourseIdOrderBySequenceNumberAsc(courseId)
                .stream()
                .map(module -> new AdminModuleResponse(
                        module.getId(),
                        course.getId(),
                        course.getTitle(),
                        module.getTitle(),
                        module.getDescription(),
                        module.getSequenceNumber(),
                        module.isPublished(),
                        module.getCreatedAt(),
                        module.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional
    public AdminExamResponse updateExam(
            Long examId,
            AdminExamUpdateRequest request
    ) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found with ID: " + examId
                ));

        if (request.name() != null) {
            if (request.name().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Exam name cannot be blank"
                );
            }
            exam.setName(request.name().trim());
        }

        if (request.code() != null) {
            if (request.code().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Exam code cannot be blank"
                );
            }

            String code = request.code().trim();

            if (examRepository.existsByCodeIgnoreCaseAndIdNot(
                    code, examId
            )) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "An exam with this code already exists"
                );
            }

            exam.setCode(code);
        }

        if (request.category() != null) {
            try {
                exam.setCategory(ExamCategory.valueOf(
                        request.category().trim().toUpperCase()
                ));
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid exam category: " + request.category()
                );
            }
        }

        if (request.conductingBody() != null) {
            exam.setConductingBody(request.conductingBody().trim());
        }

        if (request.description() != null) {
            exam.setDescription(request.description().trim());
        }

        if (request.officialWebsite() != null) {
            exam.setOfficialWebsite(request.officialWebsite().trim());
        }

        Exam updatedExam = examRepository.save(exam);

        return new AdminExamResponse(
                updatedExam.getId(),
                updatedExam.getName(),
                updatedExam.getCode(),
                updatedExam.getCategory().name(),
                updatedExam.getConductingBody(),
                updatedExam.getDescription(),
                updatedExam.getOfficialWebsite(),
                updatedExam.isActive(),
                updatedExam.getCreatedAt(),
                updatedExam.getUpdatedAt()
        );
    }

    @Transactional
    public AdminUserResponse updateUserRole(
            Long userId,
            Role newRole,
            AppUser currentUser
    ) {
        if (currentUser.getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot change your own role"
            );
        }

        AppUser targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Only super admins can assign the SUPER_ADMIN role.
        if (newRole == Role.SUPER_ADMIN
                && currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only super admins can assign the super admin role"
            );
        }

        // An ordinary admin cannot modify an existing super admin.
        if (currentUser.getRole() == Role.ADMIN
                && targetUser.getRole() == Role.SUPER_ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot modify a super admin account"
            );
        }

        // Prevent demoting the last enabled super admin.
        if (targetUser.getRole() == Role.SUPER_ADMIN
                && targetUser.isEnabled()
                && newRole != Role.SUPER_ADMIN
                && userRepository.countByRoleAndEnabledTrue(
                Role.SUPER_ADMIN
        ) <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot remove the last enabled super admin"
            );
        }

        Role oldRole = targetUser.getRole();

        targetUser.setRole(newRole);

        AppUser savedUser = userRepository.save(targetUser);

        adminAuditLogRepository.save(
                new AdminAuditLog(
                        currentUser,
                        savedUser,
                        "USER_ROLE_UPDATED",
                        oldRole.name(),
                        newRole.name()
                )
        );

        return toAdminUserResponse(savedUser);
    }

    @Transactional
    public int sendAnnouncementToAllStudents(
            AdminAnnouncementRequest request
    ) {
        List<AppUser> students =
                userRepository.findByRoleAndEnabledTrueOrderByCreatedAtDesc(
                        Role.STUDENT
                );

        return notificationService.sendAnnouncementToAllStudents(
                request,
                students
        );
    }

    @Transactional(readOnly = true)
    public byte[] exportPlatformOverviewCsv() {

        AdminDashboardResponse dashboard = getDashboardStatistics();
        AdminReviewAnalyticsResponse reviews = getAdminReviewAnalytics();
        AdminNotificationAnalyticsResponse notifications =
                getAdminNotificationAnalytics();

        StringBuilder csv = new StringBuilder();

        csv.append("Category,Metric,Value\n");

        // User statistics
        csv.append("Users,Total Users,")
                .append(dashboard.totalUsers()).append('\n');
        csv.append("Users,Total Students,")
                .append(dashboard.totalStudents()).append('\n');
        csv.append("Users,Total Instructors,")
                .append(dashboard.totalInstructors()).append('\n');
        csv.append("Users,Total Admins,")
                .append(dashboard.totalAdmins()).append('\n');
        csv.append("Users,Total Super Admins,")
                .append(dashboard.totalSuperAdmins()).append('\n');
        csv.append("Users,Enabled Users,")
                .append(dashboard.enabledUsers()).append('\n');
        csv.append("Users,Active Students,")
                .append(dashboard.activeStudents()).append('\n');

        // Course statistics
        csv.append("Courses,Total Courses,")
                .append(dashboard.totalCourses()).append('\n');
        csv.append("Courses,Active Courses,")
                .append(dashboard.activeCourses()).append('\n');

        // Enrollment statistics
        csv.append("Enrollments,Total Enrollments,")
                .append(dashboard.totalEnrollments()).append('\n');

        // Quiz statistics
        csv.append("Quizzes,Total Quizzes,")
                .append(dashboard.totalQuizzes()).append('\n');
        csv.append("Quizzes,Published Quizzes,")
                .append(dashboard.publishedQuizzes()).append('\n');
        csv.append("Quizzes,Total Quiz Attempts,")
                .append(dashboard.totalQuizAttempts()).append('\n');

        // Review statistics
        csv.append("Reviews,Total Reviews,")
                .append(reviews.totalReviews()).append('\n');
        csv.append("Reviews,Average Rating,")
                .append(reviews.averageRating()).append('\n');

        for (Map.Entry<Short, Long> entry :
                reviews.ratingDistribution().entrySet()) {

            csv.append("Reviews,")
                    .append(entry.getKey())
                    .append(" Star Reviews,")
                    .append(entry.getValue())
                    .append('\n');
        }

        // Notification statistics
        csv.append("Notifications,Total Notifications,")
                .append(notifications.totalNotifications()).append('\n');
        csv.append("Notifications,Read Notifications,")
                .append(notifications.readNotifications()).append('\n');
        csv.append("Notifications,Unread Notifications,")
                .append(notifications.unreadNotifications()).append('\n');
        csv.append("Notifications,Read Percentage,")
                .append(notifications.readPercentage()).append("%\n");

        for (Map.Entry<String, Long> entry :
                notifications.notificationTypeDistribution().entrySet()) {

            csv.append("Notifications,")
                    .append(entry.getKey())
                    .append(',')
                    .append(entry.getValue())
                    .append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] exportRegistrationAndEnrollmentTrendsCsv() {

        List<DailyRegistrationResponse> registrations =
                getDailyStudentRegistrations();

        List<DailyEnrollmentResponse> enrollments =
                getDailyEnrollments();

        StringBuilder csv = new StringBuilder();

        csv.append("Date,Student Registrations,Course Enrollments\n");

        for (int i = 0; i < 7; i++) {

            LocalDate date = LocalDate.now().minusDays(6 - i);

            long registrationCount = registrations.stream()
                    .filter(item -> item.date().equals(date))
                    .mapToLong(DailyRegistrationResponse::registrations)
                    .findFirst()
                    .orElse(0L);

            long enrollmentCount = enrollments.stream()
                    .filter(item -> item.date().equals(date))
                    .mapToLong(DailyEnrollmentResponse::enrollments)
                    .findFirst()
                    .orElse(0L);

            csv.append(date).append(',')
                    .append(registrationCount).append(',')
                    .append(enrollmentCount).append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    public AdminCourseResponse createCourse(CreateCourseRequest request) {

        if (request == null || request.title() == null
                || request.title().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Course title is required"
            );
        }

        if (request.examId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exam ID is required"
            );
        }

        Exam exam = examRepository.findById(request.examId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found with ID: " + request.examId()
                ));

        Course course = new Course();

        String baseSlug = request.title()
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (baseSlug.isBlank()) {
            baseSlug = "course";
        }

        // Keep the slug within the Course entity's 220-character limit.
        baseSlug = baseSlug.substring(0, Math.min(baseSlug.length(), 200));

        String slug = baseSlug;
        int suffix = 1;

        while (courseRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix++;
        }

        course.setTitle(request.title().trim());
        course.setSlug(slug);
        course.setDescription(request.description());
        course.setExam(exam);
        course.setInstructorName(request.instructorName() == null
                ? null : request.instructorName().trim());
        course.setDifficulty(request.difficulty());
        course.setLanguage(request.language());
        course.setThumbnailUrl(request.thumbnailUrl() == null
                ? null : request.thumbnailUrl().trim());
        course.setYoutubePlaylistId(request.youtubePlaylistId() == null
                ? null : request.youtubePlaylistId().trim());
        course.setActive(true);

        Course savedCourse = courseRepository.save(course);

        return new AdminCourseResponse(
                savedCourse.getId(),
                savedCourse.getTitle(),
                savedCourse.getSlug(),
                savedCourse.getInstructorName(),
                savedCourse.getDifficulty() != null
                        ? savedCourse.getDifficulty().name() : null,
                savedCourse.getLanguage() != null
                        ? savedCourse.getLanguage().name() : null,
                savedCourse.isActive(),
                savedCourse.getCreatedAt()
        );
    }

    @Transactional
    public NotificationResponse sendNotificationToStudent(
            AdminStudentNotificationRequest request
    ) {
        AppUser student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found"
                ));

        if (student.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Notifications can only be sent to student accounts"
            );
        }

        if (!student.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot send notification to a disabled student account"
            );
        }

        return notificationService.sendNotificationToStudent(
                student.getId(),
                request.title(),
                request.message(),
                request.type()
        );
    }


    @Transactional(readOnly = true)
    public Page<AdminNotificationResponse> getAdminNotificationHistory(
            Pageable pageable
    ) {
        return notificationRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(notification -> new AdminNotificationResponse(
                        notification.getId(),
                        notification.getUser().getId(),
                        notification.getUser().getName(),
                        notification.getUser().getEmail(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getType(),
                        notification.getReferenceId(),
                        notification.isRead(),
                        notification.getCreatedAt(),
                        notification.getReadAt()
                ));
    }

    @Transactional
    public void deleteCourse(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        // Prevent deletion if students are enrolled.
        if (enrollmentRepository.countByCourseId(courseId) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a course with existing enrollments"
            );
        }

        // Prevent deletion if the course contains quizzes.
        if (quizRepository.existsByCourseId(courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a course containing quizzes"
            );
        }

        if (courseModuleRepository.existsByCourseId(courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete course because it contains modules. Delete its curriculum first."
            );
        }

        // Prevent deletion if course reviews exist.
        if (courseReviewRepository.existsByCourseId(courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a course with existing reviews"
            );
        }

        courseRepository.delete(course);
    }

    @Transactional(readOnly = true)
    public Page<AdminEnrollmentResponse> getAdminEnrollments(
            Pageable pageable,
            EnrollmentStatus status
    ) {
        Page<CourseEnrollment> enrollments;

        if (status == null) {
            enrollments = enrollmentRepository.findAll(
                    PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by(Sort.Direction.DESC, "enrolledAt")
                    )
            );
        } else {
            enrollments = enrollmentRepository
                    .findByStatusOrderByEnrolledAtDesc(status, pageable);
        }

        return enrollments.map(enrollment ->
                new AdminEnrollmentResponse(
                        enrollment.getId(),
                        enrollment.getUser().getId(),
                        enrollment.getCourse().getId(),
                        enrollment.getCourse().getTitle(),
                        enrollment.getStatus(),
                        enrollment.getEnrolledAt(),
                        enrollment.getCompletedAt()
                )
        );
    }

    @Transactional
    public AdminCourseResponse updateCourseStatus(
            Long courseId,
            Boolean active
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        course.setActive(active);

        Course savedCourse = courseRepository.save(course);

        return new AdminCourseResponse(
                savedCourse.getId(),
                savedCourse.getTitle(),
                savedCourse.getSlug(),
                savedCourse.getInstructorName(),
                savedCourse.getDifficulty() != null
                        ? savedCourse.getDifficulty().name() : null,
                savedCourse.getLanguage() != null
                        ? savedCourse.getLanguage().name() : null,
                savedCourse.isActive(),
                savedCourse.getCreatedAt()
        );
    }


    @Transactional
    public AdminCourseResponse updateCourse(
            Long courseId,
            UpdateCourseRequest request
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        if (request.title() != null) {
            course.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            course.setDescription(request.description());
        }

        if (request.instructorName() != null) {
            course.setInstructorName(request.instructorName().trim());
        }

        if (request.difficulty() != null) {
            course.setDifficulty(request.difficulty());
        }

        if (request.language() != null) {
            course.setLanguage(request.language());
        }

        if (request.thumbnailUrl() != null) {
            course.setThumbnailUrl(request.thumbnailUrl().trim());
        }

        if (request.youtubePlaylistId() != null) {
            course.setYoutubePlaylistId(
                    request.youtubePlaylistId().trim()
            );
        }

        Course savedCourse = courseRepository.save(course);

        return new AdminCourseResponse(
                savedCourse.getId(),
                savedCourse.getTitle(),
                savedCourse.getSlug(),
                savedCourse.getInstructorName(),
                savedCourse.getDifficulty() != null
                        ? savedCourse.getDifficulty().name() : null,
                savedCourse.getLanguage() != null
                        ? savedCourse.getLanguage().name() : null,
                savedCourse.isActive(),
                savedCourse.getCreatedAt()
        );
    }

    public Page<AdminExamResponse> getAllAdminExams(
            Pageable pageable
    ) {
        return examRepository.findAll(pageable)
                .map(exam -> new AdminExamResponse(
                        exam.getId(),
                        exam.getName(),
                        exam.getCode(),
                        exam.getCategory().name(),
                        exam.getConductingBody(),
                        exam.getDescription(),
                        exam.getOfficialWebsite(),
                        exam.isActive(),
                        exam.getCreatedAt(),
                        exam.getUpdatedAt()
                ));
    }

    @Transactional
    public AdminModuleResponse createCourseModule(
            Long courseId,
            AdminModuleCreateRequest request
    ) {
        // Validate module title
        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Module title is required"
            );
        }

        // Validate sequence number
        if (request.sequenceNumber() == null || request.sequenceNumber() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sequence number must be greater than zero"
            );
        }

        // Find parent course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found with ID: " + courseId
                ));

        // Prevent duplicate sequence numbers within the course
        if (courseModuleRepository.existsByCourseIdAndSequenceNumber(
                courseId,
                request.sequenceNumber()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This sequence number is already used in the course"
            );
        }

        CourseModule module = new CourseModule();

        module.setCourse(course);
        module.setTitle(request.title().trim());
        module.setDescription(request.description());
        module.setSequenceNumber(request.sequenceNumber());
        module.setPublished(
                Boolean.TRUE.equals(request.published())
        );

        CourseModule savedModule = courseModuleRepository.save(module);

        return new AdminModuleResponse(
                savedModule.getId(),
                course.getId(),
                course.getTitle(),
                savedModule.getTitle(),
                savedModule.getDescription(),
                savedModule.getSequenceNumber(),
                savedModule.isPublished(),
                savedModule.getCreatedAt(),
                savedModule.getUpdatedAt()
        );
    }

    @Transactional
    public AdminExamResponse createExam(AdminExamCreateRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exam name is required"
            );
        }

        if (request.code() == null || request.code().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exam code is required"
            );
        }

        if (request.category() == null || request.category().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exam category is required"
            );
        }

        String code = request.code().trim();

        if (examRepository.existsByCodeIgnoreCase(code)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An exam with this code already exists"
            );
        }

        ExamCategory category;

        try {
            category = ExamCategory.valueOf(
                    request.category().trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid exam category: " + request.category()
            );
        }

        Exam exam = new Exam();
        exam.setName(request.name().trim());
        exam.setCode(code);
        exam.setCategory(category);
        exam.setConductingBody(request.conductingBody());
        exam.setDescription(request.description());
        exam.setOfficialWebsite(request.officialWebsite());
        exam.setActive(true);

        Exam savedExam = examRepository.save(exam);

        return new AdminExamResponse(
                savedExam.getId(),
                savedExam.getName(),
                savedExam.getCode(),
                savedExam.getCategory().name(),
                savedExam.getConductingBody(),
                savedExam.getDescription(),
                savedExam.getOfficialWebsite(),
                savedExam.isActive(),
                savedExam.getCreatedAt(),
                savedExam.getUpdatedAt()
        );
    }

    @Transactional
    public AdminModuleResponse updateCourseModule(
            Long moduleId,
            AdminModuleUpdateRequest request
    ) {
        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Module not found with ID: " + moduleId
                ));

        if (request.title() != null) {
            if (request.title().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Module title cannot be blank"
                );
            }

            module.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            module.setDescription(request.description().trim());
        }

        if (request.sequenceNumber() != null) {
            if (request.sequenceNumber() < 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Sequence number must be greater than zero"
                );
            }

            if (courseModuleRepository.existsByCourseIdAndSequenceNumberAndIdNot(
                    module.getCourse().getId(),
                    request.sequenceNumber(),
                    moduleId
            )) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This sequence number is already used in the course"
                );
            }

            module.setSequenceNumber(request.sequenceNumber());
        }

        if (request.published() != null) {
            module.setPublished(request.published());
        }

        CourseModule updatedModule = courseModuleRepository.save(module);
        Course course = updatedModule.getCourse();

        return new AdminModuleResponse(
                updatedModule.getId(),
                course.getId(),
                course.getTitle(),
                updatedModule.getTitle(),
                updatedModule.getDescription(),
                updatedModule.getSequenceNumber(),
                updatedModule.isPublished(),
                updatedModule.getCreatedAt(),
                updatedModule.getUpdatedAt()
        );
    }

    @Transactional
    public AdminModuleResponse updateModulePublishStatus(
            Long moduleId,
            boolean published
    ) {
        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Module not found with ID: " + moduleId
                ));

        module.setPublished(published);

        CourseModule updatedModule =
                courseModuleRepository.save(module);

        Course course = updatedModule.getCourse();

        return new AdminModuleResponse(
                updatedModule.getId(),
                course.getId(),
                course.getTitle(),
                updatedModule.getTitle(),
                updatedModule.getDescription(),
                updatedModule.getSequenceNumber(),
                updatedModule.isPublished(),
                updatedModule.getCreatedAt(),
                updatedModule.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteCourseModule(Long moduleId) {

        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Module not found with ID: " + moduleId
                ));

        // Prevent deletion if lectures are linked to this module
        if (lectureRepository.existsByModuleId(moduleId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this module because lectures are linked to it. "
                            + "Remove or reassign the lectures first."
            );
        }

        courseModuleRepository.delete(module);
    }

    @Transactional
    public AdminLectureResponse createLecture(
            Long moduleId,
            AdminLectureCreateRequest request
    ) {
        // Validate lecture title
        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Lecture title is required"
            );
        }

        // Validate YouTube video ID
        if (request.youtubeVideoId() == null
                || request.youtubeVideoId().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "YouTube video ID is required"
            );
        }

        // Validate sequence number
        if (request.sequenceNumber() == null
                || request.sequenceNumber() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sequence number must be greater than zero"
            );
        }

        // Validate duration if provided
        if (request.durationSeconds() != null
                && request.durationSeconds() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duration cannot be negative"
            );
        }

        // Find parent module
        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Module not found with ID: " + moduleId
                ));

        // Prevent duplicate sequence numbers within the module
        if (lectureRepository.existsByModuleIdAndSequenceNumber(
                moduleId,
                request.sequenceNumber()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This sequence number is already used in the module"
            );
        }

        Lecture lecture = new Lecture();

        lecture.setModule(module);
        lecture.setTitle(request.title().trim());
        lecture.setDescription(request.description());
        lecture.setYoutubeVideoId(request.youtubeVideoId().trim());
        lecture.setDurationSeconds(request.durationSeconds());
        lecture.setSequenceNumber(request.sequenceNumber());
        lecture.setPublished(Boolean.TRUE.equals(request.published()));

        Lecture savedLecture = lectureRepository.save(lecture);

        Course course = module.getCourse();

        return new AdminLectureResponse(
                savedLecture.getId(),
                module.getId(),
                module.getTitle(),
                course.getId(),
                course.getTitle(),
                savedLecture.getTitle(),
                savedLecture.getDescription(),
                savedLecture.getYoutubeVideoId(),
                savedLecture.getDurationSeconds(),
                savedLecture.getSequenceNumber(),
                savedLecture.isPublished(),
                savedLecture.getCreatedAt(),
                savedLecture.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<AdminLectureResponse> getModuleLectures(Long moduleId) {

        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Module not found with ID: " + moduleId
                ));

        Course course = module.getCourse();

        return lectureRepository
                .findByModuleIdOrderBySequenceNumberAsc(moduleId)
                .stream()
                .map(lecture -> new AdminLectureResponse(
                        lecture.getId(),
                        module.getId(),
                        module.getTitle(),
                        course.getId(),
                        course.getTitle(),
                        lecture.getTitle(),
                        lecture.getDescription(),
                        lecture.getYoutubeVideoId(),
                        lecture.getDurationSeconds(),
                        lecture.getSequenceNumber(),
                        lecture.isPublished(),
                        lecture.getCreatedAt(),
                        lecture.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional
    public AdminLectureResponse updateLecture(
            Long lectureId,
            AdminLectureUpdateRequest request
    ) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lecture not found with ID: " + lectureId
                ));

        if (request.title() != null) {
            if (request.title().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Lecture title cannot be blank"
                );
            }

            lecture.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            lecture.setDescription(request.description().trim());
        }

        if (request.youtubeVideoId() != null) {
            if (request.youtubeVideoId().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "YouTube video ID cannot be blank"
                );
            }

            lecture.setYoutubeVideoId(request.youtubeVideoId().trim());
        }

        if (request.durationSeconds() != null) {
            if (request.durationSeconds() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duration cannot be negative"
                );
            }

            lecture.setDurationSeconds(request.durationSeconds());
        }

        if (request.sequenceNumber() != null) {
            if (request.sequenceNumber() < 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Sequence number must be greater than zero"
                );
            }

            Long moduleId = lecture.getModule().getId();

            if (lectureRepository.existsByModuleIdAndSequenceNumberAndIdNot(
                    moduleId,
                    request.sequenceNumber(),
                    lectureId
            )) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This sequence number is already used in the module"
                );
            }

            lecture.setSequenceNumber(request.sequenceNumber());
        }

        if (request.published() != null) {
            lecture.setPublished(request.published());
        }

        Lecture updatedLecture = lectureRepository.save(lecture);

        CourseModule module = updatedLecture.getModule();
        Course course = module.getCourse();

        return new AdminLectureResponse(
                updatedLecture.getId(),
                module.getId(),
                module.getTitle(),
                course.getId(),
                course.getTitle(),
                updatedLecture.getTitle(),
                updatedLecture.getDescription(),
                updatedLecture.getYoutubeVideoId(),
                updatedLecture.getDurationSeconds(),
                updatedLecture.getSequenceNumber(),
                updatedLecture.isPublished(),
                updatedLecture.getCreatedAt(),
                updatedLecture.getUpdatedAt()
        );
    }

    @Transactional
    public AdminLectureResponse updateLecturePublishStatus(
            Long lectureId,
            boolean published
    ) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lecture not found with ID: " + lectureId
                ));

        lecture.setPublished(published);

        Lecture updatedLecture = lectureRepository.save(lecture);

        CourseModule module = updatedLecture.getModule();
        Course course = module.getCourse();

        return new AdminLectureResponse(
                updatedLecture.getId(),
                module.getId(),
                module.getTitle(),
                course.getId(),
                course.getTitle(),
                updatedLecture.getTitle(),
                updatedLecture.getDescription(),
                updatedLecture.getYoutubeVideoId(),
                updatedLecture.getDurationSeconds(),
                updatedLecture.getSequenceNumber(),
                updatedLecture.isPublished(),
                updatedLecture.getCreatedAt(),
                updatedLecture.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteLecture(Long lectureId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lecture not found with ID: " + lectureId
                ));

        // Check student learning progress
        if (lectureProgressRepository.existsByLectureId(lectureId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete lecture because student progress exists."
            );
        }

        // Check student bookmarks
        if (lectureBookmarkRepository.existsByLectureId(lectureId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete lecture because student bookmarks exist."
            );
        }

        // Check student notes
        if (lectureNoteRepository.existsByLectureId(lectureId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete lecture because student notes exist."
            );
        }

        // Check associated quizzes
        if (quizRepository.existsByLectureId(lectureId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete lecture because quizzes are associated with it."
            );
        }

        lectureRepository.delete(lecture);
    }

    @Transactional
    public void deleteExam(Long examId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found with ID: " + examId
                ));

        if (courseRepository.existsByExamId(examId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this exam because courses are linked to it. "
                            + "Remove or reassign those courses first."
            );
        }

        examRepository.delete(exam);
    }


    private AdminUserResponse toAdminUserResponse(AppUser user) {
        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }

    private AdminAuditLogResponse toAuditLogResponse(
            AdminAuditLog log
    ) {
        return new AdminAuditLogResponse(
                log.getId(),
                log.getAdmin() != null
                        ? log.getAdmin().getId() : null,
                log.getAdmin() != null
                        ? log.getAdmin().getName() : null,
                log.getTargetUser() != null
                        ? log.getTargetUser().getId() : null,
                log.getTargetUser() != null
                        ? log.getTargetUser().getName() : null,
                log.getAction(),
                log.getOldValue(),
                log.getNewValue(),
                log.getCreatedAt()
        );
    }


}