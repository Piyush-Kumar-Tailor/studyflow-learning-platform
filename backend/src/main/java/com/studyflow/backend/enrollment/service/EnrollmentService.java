package com.studyflow.backend.enrollment.service;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.course.repository.CourseRepository;
import com.studyflow.backend.enrollment.dto.EnrollmentResponse;
import com.studyflow.backend.enrollment.dto.UpdateEnrollmentStatusRequest;
import com.studyflow.backend.enrollment.entity.CourseEnrollment;
import com.studyflow.backend.enrollment.enums.EnrollmentStatus;
import com.studyflow.backend.enrollment.repository.CourseEnrollmentRepository;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.studyflow.backend.notification.dto.CreateNotificationRequest;
import com.studyflow.backend.notification.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public EnrollmentService(
            CourseEnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public EnrollmentResponse enroll(Long userId, Long courseId) {

        AppUser user = userRepository.findById(userId)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found or disabled"
                ));

        Course course = courseRepository.findById(courseId)
                .filter(Course::isActive)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Course not found or inactive"
                ));

        if (enrollmentRepository.existsByUserIdAndCourseId(
                userId, courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already enrolled in this course"
            );
        }

        CourseEnrollment enrollment = new CourseEnrollment(user, course);

        try {
            CourseEnrollment savedEnrollment =
                    enrollmentRepository.saveAndFlush(enrollment);

            notificationService.createNotification(
                    userId,
                    new CreateNotificationRequest(
                            "Enrollment Successful",
                            "You have successfully enrolled in "
                                    + course.getTitle()
                                    + ". Start learning now!",
                            "ENROLLMENT",
                            course.getId()
                    )
            );

            return EnrollmentResponse.from(savedEnrollment);

        } catch (DataIntegrityViolationException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already enrolled in this course"
            );
        }
    }

    public List<EnrollmentResponse> getMyEnrollments(Long userId) {
        return enrollmentRepository
                .findByUserIdOrderByEnrolledAtDesc(
                        userId,
                        org.springframework.data.domain.Pageable.unpaged()
                )
                .getContent()
                .stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    public EnrollmentResponse getMyEnrollment(
            Long userId,
            Long enrollmentId
    ) {
        return EnrollmentResponse.from(findOwnedEnrollment(
                enrollmentId, userId
        ));
    }

    @Transactional
    public EnrollmentResponse updateStatus(
            Long userId,
            Long enrollmentId,
            UpdateEnrollmentStatusRequest request
    ) {
        CourseEnrollment enrollment = findOwnedEnrollment(
                enrollmentId, userId
        );

        EnrollmentStatus requestedStatus = request.status();

        if (requestedStatus == EnrollmentStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Course completion is calculated from lecture progress"
            );
        }

        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A completed enrollment cannot be changed"
            );
        }

        enrollment.setStatus(requestedStatus);

        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    private CourseEnrollment findOwnedEnrollment(
            Long enrollmentId,
            Long userId
    ) {
        return enrollmentRepository
                .findByIdAndUserId(enrollmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Enrollment not found"
                ));
    }
}