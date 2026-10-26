package com.studyflow.backend.enrollment.service;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.curriculum.entity.CourseModule;
import com.studyflow.backend.curriculum.entity.Lecture;
import com.studyflow.backend.curriculum.repository.LectureRepository;
import com.studyflow.backend.enrollment.dto.CourseProgressResponse;
import com.studyflow.backend.enrollment.dto.ContinueLearningResponse;
import com.studyflow.backend.enrollment.dto.LectureProgressResponse;
import com.studyflow.backend.enrollment.dto.UpdateLectureProgressRequest;
import com.studyflow.backend.enrollment.entity.CourseEnrollment;
import com.studyflow.backend.enrollment.entity.LectureProgress;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import com.studyflow.backend.enrollment.enums.LectureProgressStatus;
import com.studyflow.backend.enrollment.repository.CourseEnrollmentRepository;
import com.studyflow.backend.enrollment.repository.LectureProgressRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LectureProgressService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final LectureProgressRepository progressRepository;
    private final LectureRepository lectureRepository;

    public LectureProgressService(
            CourseEnrollmentRepository enrollmentRepository,
            LectureProgressRepository progressRepository,
            LectureRepository lectureRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.progressRepository = progressRepository;
        this.lectureRepository = lectureRepository;
    }

    // =========================================================
    // UPDATE LECTURE PROGRESS
    // =========================================================

    @Transactional
    public LectureProgressResponse updateProgress(
            Long userId,
            Long enrollmentId,
            Long lectureId,
            UpdateLectureProgressRequest request
    ) {
        CourseEnrollment enrollment = findOwnedEnrollment(
                enrollmentId,
                userId
        );

        if (enrollment.getStatus() != EnrollmentStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lecture progress can only be updated for active enrollments"
            );
        }

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lecture not found"
                ));

        CourseModule module = lecture.getModule();
        Course course = module.getCourse();

        if (!lecture.isPublished()
                || !module.isPublished()
                || !course.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Lecture is unavailable"
            );
        }

        if (!enrollment.getCourse().getId().equals(course.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Lecture does not belong to this enrollment's course"
            );
        }

        Integer duration = lecture.getDurationSeconds();
        int position = request.lastPositionSeconds();

        if (duration != null && position > duration) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Video position cannot exceed lecture duration"
            );
        }

        LectureProgress progress = progressRepository
                .findByEnrollmentIdAndLectureId(
                        enrollmentId,
                        lectureId
                )
                .orElseGet(() -> new LectureProgress(enrollment, lecture));

        // Completed lectures remain completed.
        if (progress.getStatus() == LectureProgressStatus.COMPLETED) {
            return LectureProgressResponse.from(progress);
        }

        progress.setLastPositionSeconds(position);

        if (Boolean.TRUE.equals(request.completed())) {
            progress.setStatus(LectureProgressStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());

            if (duration != null) {
                progress.setLastPositionSeconds(duration);
            }
        } else {
            progress.setStatus(
                    position == 0
                            ? LectureProgressStatus.NOT_STARTED
                            : LectureProgressStatus.IN_PROGRESS
            );

            progress.setCompletedAt(null);
        }

        LectureProgress saved = progressRepository.save(progress);

        updateEnrollmentCompletion(enrollment);

        return LectureProgressResponse.from(saved);
    }

    // =========================================================
    // GET COURSE PROGRESS SUMMARY
    // =========================================================

    public CourseProgressResponse getCourseProgress(
            Long userId,
            Long enrollmentId
    ) {
        CourseEnrollment enrollment = findOwnedEnrollment(
                enrollmentId,
                userId
        );

        Long courseId = enrollment.getCourse().getId();

        long totalLectures =
                lectureRepository.countPublishedLecturesByCourseId(courseId);

        long completedLectures =
                progressRepository.countPublishedCompletedProgress(
                        enrollmentId,
                        LectureProgressStatus.COMPLETED
                );

        double percentage = totalLectures == 0
                ? 0.0
                : Math.round(
                (completedLectures * 10000.0) / totalLectures
        ) / 100.0;

        return new CourseProgressResponse(
                enrollment.getId(),
                courseId,
                enrollment.getCourse().getTitle(),
                enrollment.getStatus(),
                totalLectures,
                completedLectures,
                percentage
        );
    }

    // =========================================================
    // GET INDIVIDUAL LECTURE PROGRESS
    // =========================================================

    public List<LectureProgressResponse> getLectureProgress(
            Long userId,
            Long enrollmentId
    ) {
        findOwnedEnrollment(enrollmentId, userId);

        return progressRepository
                .findByEnrollmentIdOrderByUpdatedAtDesc(enrollmentId)
                .stream()
                .filter(this::isLectureCurrentlyPublished)
                .map(LectureProgressResponse::from)
                .toList();
    }

    // =========================================================
    // CONTINUE LEARNING
    // =========================================================

    public ContinueLearningResponse getContinueLearning(Long userId) {

        List<CourseEnrollment> enrollments = enrollmentRepository
                .findByUserIdOrderByEnrolledAtDesc(
                        userId,
                        Pageable.unpaged()
                )
                .getContent();

        return enrollments.stream()
                .filter(enrollment ->
                        enrollment.getStatus() == EnrollmentStatus.IN_PROGRESS
                )
                .flatMap(enrollment ->
                        progressRepository
                                .findByEnrollmentIdOrderByUpdatedAtDesc(
                                        enrollment.getId()
                                )
                                .stream()
                                .filter(progress ->
                                        progress.getStatus()
                                                == LectureProgressStatus.IN_PROGRESS
                                )
                                .filter(this::isLectureCurrentlyPublished)
                                .map(progress -> new ContinueLearningItem(
                                        enrollment,
                                        progress
                                ))
                )
                .max(Comparator.comparing(
                        item -> item.progress().getUpdatedAt(),
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .map(item -> new ContinueLearningResponse(
                        item.enrollment().getId(),
                        item.enrollment().getCourse().getId(),
                        item.enrollment().getCourse().getTitle(),
                        item.progress().getLecture().getId(),
                        item.progress().getLecture().getTitle(),
                        item.progress().getLecture().getYoutubeVideoId(),
                        item.progress().getLastPositionSeconds(),
                        item.progress().getStatus()
                ))
                .orElse(null);
    }

    // =========================================================
    // AUTOMATIC ENROLLMENT COMPLETION
    // =========================================================

    private void updateEnrollmentCompletion(
            CourseEnrollment enrollment
    ) {
        Long courseId = enrollment.getCourse().getId();

        long totalLectures =
                lectureRepository.countPublishedLecturesByCourseId(courseId);

        // Do not complete courses that have no published lectures.
        if (totalLectures == 0) {
            return;
        }

        long completedLectures =
                progressRepository.countPublishedCompletedProgress(
                        enrollment.getId(),
                        LectureProgressStatus.COMPLETED
                );

        if (completedLectures >= totalLectures) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
        }
    }

    // =========================================================
    // VALIDATE ENROLLMENT OWNERSHIP
    // =========================================================

    private CourseEnrollment findOwnedEnrollment(
            Long enrollmentId,
            Long userId
    ) {
        return enrollmentRepository
                .findByIdAndUserId(enrollmentId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Enrollment not found"
                ));
    }

    // =========================================================
    // CHECK WHETHER A LECTURE IS CURRENTLY AVAILABLE
    // =========================================================

    private boolean isLectureCurrentlyPublished(
            LectureProgress progress
    ) {
        Lecture lecture = progress.getLecture();
        CourseModule module = lecture.getModule();
        Course course = module.getCourse();

        return lecture.isPublished()
                && module.isPublished()
                && course.isActive();
    }

    private record ContinueLearningItem(
            CourseEnrollment enrollment,
            LectureProgress progress
    ) {
    }
}