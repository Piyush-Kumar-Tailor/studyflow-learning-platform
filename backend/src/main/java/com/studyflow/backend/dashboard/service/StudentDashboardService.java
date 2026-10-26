package com.studyflow.backend.dashboard.service;

import com.studyflow.backend.dashboard.dto.StudentDashboardResponse;
import com.studyflow.backend.enrollment.dto.ContinueLearningResponse;
import com.studyflow.backend.enrollment.entity.CourseEnrollment;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import com.studyflow.backend.enrollment.enums.LectureProgressStatus;
import com.studyflow.backend.enrollment.repository.CourseEnrollmentRepository;
import com.studyflow.backend.enrollment.repository.LectureProgressRepository;
import com.studyflow.backend.curriculum.repository.LectureRepository;
import com.studyflow.backend.enrollment.service.LectureProgressService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentDashboardService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final LectureProgressRepository progressRepository;
    private final LectureRepository lectureRepository;
    private final LectureProgressService lectureProgressService;

    public StudentDashboardService(
            CourseEnrollmentRepository enrollmentRepository,
            LectureProgressRepository progressRepository,
            LectureRepository lectureRepository,
            LectureProgressService lectureProgressService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.progressRepository = progressRepository;
        this.lectureRepository = lectureRepository;
        this.lectureProgressService = lectureProgressService;
    }

    public StudentDashboardResponse getDashboard(Long userId) {

        // Fetch the student's enrollments, newest first.
        List<CourseEnrollment> enrollments = enrollmentRepository
                .findByUserIdOrderByEnrolledAtDesc(
                        userId,
                        Pageable.unpaged()
                )
                .getContent();

        long totalEnrolledCourses = enrollments.size();

        long inProgressCourses = enrollments.stream()
                .filter(enrollment ->
                        enrollment.getStatus()
                                == EnrollmentStatus.IN_PROGRESS
                )
                .count();

        long completedCourses = enrollments.stream()
                .filter(enrollment ->
                        enrollment.getStatus()
                                == EnrollmentStatus.COMPLETED
                )
                .count();

        // Count completed lectures across all enrollments.
        long totalLecturesCompleted = enrollments.stream()
                .mapToLong(enrollment ->
                        progressRepository.countPublishedCompletedProgress(
                                enrollment.getId(),
                                LectureProgressStatus.COMPLETED
                        )
                )
                .sum();

        // The repository query returns enrollments newest first.
        // Keep the five most recently enrolled courses.
        List<StudentDashboardResponse.RecentCourseProgress> recentCourses =
                enrollments.stream()
                        .limit(5)
                        .map(enrollment -> {

                            Long courseId =
                                    enrollment.getCourse().getId();

                            long totalLectures =
                                    lectureRepository
                                            .countPublishedLecturesByCourseId(
                                                    courseId
                                            );

                            long completedLectures =
                                    progressRepository
                                            .countPublishedCompletedProgress(
                                                    enrollment.getId(),
                                                    LectureProgressStatus.COMPLETED
                                            );

                            double progressPercentage =
                                    totalLectures == 0
                                            ? 0.0
                                            : Math.round(
                                            completedLectures
                                            * 10000.0
                                            / totalLectures
                                    ) / 100.0;

                            return new StudentDashboardResponse
                                    .RecentCourseProgress(
                                    enrollment.getId(),
                                    courseId,
                                    enrollment.getCourse().getTitle(),
                                    enrollment.getStatus(),
                                    totalLectures,
                                    completedLectures,
                                    progressPercentage
                            );
                        })
                        .toList();

        // Reuse the existing Continue Learning implementation.
        ContinueLearningResponse continueLearning =
                lectureProgressService.getContinueLearning(userId);

        return new StudentDashboardResponse(
                totalEnrolledCourses,
                inProgressCourses,
                completedCourses,
                totalLecturesCompleted,
                recentCourses,
                continueLearning
        );
    }
}