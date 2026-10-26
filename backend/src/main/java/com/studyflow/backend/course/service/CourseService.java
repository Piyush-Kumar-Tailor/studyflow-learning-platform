package com.studyflow.backend.course.service;

import com.studyflow.backend.course.dto.CourseRequest;
import com.studyflow.backend.course.dto.CourseResponse;
import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.course.repository.CourseRepository;
import com.studyflow.backend.exam.entity.Exam;
import com.studyflow.backend.exam.repository.ExamRepository;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.SkillLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;

    public CourseService(
            CourseRepository courseRepository,
            ExamRepository examRepository
    ) {
        this.courseRepository = courseRepository;
        this.examRepository = examRepository;
    }

    // Public: retrieve and filter active courses
    public Page<CourseResponse> getCourses(
            Long examId,
            String search,
            SkillLevel difficulty,
            LanguagePreference language,
            int page,
            int size
    ) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "title")
        );

        String searchTerm = search == null ? "" : search.trim();

        /*
         * Build the filters dynamically so exam, title, difficulty,
         * and language can be combined.
         */
        Page<Course> courses = courseRepository.findAll(
                (root, query, criteriaBuilder) -> {

                    var predicates = new java.util.ArrayList<
                            jakarta.persistence.criteria.Predicate
                            >();

                    predicates.add(
                            criteriaBuilder.isTrue(root.get("active"))
                    );

                    if (examId != null) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("exam").get("id"),
                                        examId
                                )
                        );
                    }

                    if (!searchTerm.isEmpty()) {
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("title")
                                        ),
                                        "%" + escapeLike(searchTerm.toLowerCase(Locale.ROOT)) + "%",
                                        '\\'
                                )
                        );
                    }

                    if (difficulty != null) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("difficulty"),
                                        difficulty
                                )
                        );
                    }

                    if (language != null) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("language"),
                                        language
                                )
                        );
                    }

                    return criteriaBuilder.and(
                            predicates.toArray(
                                    new jakarta.persistence.criteria.Predicate[0]
                            )
                    );
                },
                pageable
        );

        return courses.map(CourseResponse::from);
    }

    // Public: retrieve a single active course by slug
    public CourseResponse getCourseBySlug(String slug) {

        Course course = courseRepository.findBySlugIgnoreCase(slug)
                .filter(Course::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active course not found"
                ));

        return CourseResponse.from(course);
    }

    // Admin: retrieve a course, including inactive courses
    public CourseResponse getCourseByIdForAdmin(Long id) {
        return CourseResponse.from(findCourseOrThrow(id));
    }

    // Admin: create a course
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {

        String normalizedSlug = normalizeSlug(request.slug());

        if (courseRepository.existsBySlugIgnoreCase(normalizedSlug)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A course with this slug already exists"
            );
        }

        Exam exam = findExamOrThrow(request.examId());

        Course course = new Course();

        applyRequest(course, request, normalizedSlug, exam);

        course.setActive(true);

        Course savedCourse = courseRepository.save(course);

        return CourseResponse.from(savedCourse);
    }

    // Admin: update a course
    @Transactional
    public CourseResponse updateCourse(
            Long id,
            CourseRequest request
    ) {

        Course course = findCourseOrThrow(id);

        String normalizedSlug = normalizeSlug(request.slug());

        if (courseRepository.existsBySlugIgnoreCaseAndIdNot(
                normalizedSlug,
                id
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another course already uses this slug"
            );
        }

        Exam exam = findExamOrThrow(request.examId());

        applyRequest(course, request, normalizedSlug, exam);

        Course updatedCourse = courseRepository.save(course);

        return CourseResponse.from(updatedCourse);
    }

    // Admin: activate or deactivate a course
    @Transactional
    public CourseResponse updateCourseStatus(
            Long id,
            boolean active
    ) {

        Course course = findCourseOrThrow(id);

        course.setActive(active);

        Course updatedCourse = courseRepository.save(course);

        return CourseResponse.from(updatedCourse);
    }

    private void applyRequest(
            Course course,
            CourseRequest request,
            String normalizedSlug,
            Exam exam
    ) {

        course.setTitle(request.title().trim());
        course.setSlug(normalizedSlug);
        course.setDescription(normalizeOptionalText(request.description()));
        course.setExam(exam);

        course.setInstructorName(
                normalizeOptionalText(request.instructorName())
        );

        course.setDifficulty(request.difficulty());
        course.setLanguage(request.language());

        course.setThumbnailUrl(
                normalizeOptionalText(request.thumbnailUrl())
        );

        course.setYoutubePlaylistId(
                normalizeOptionalText(request.youtubePlaylistId())
        );
    }

    private Course findCourseOrThrow(Long id) {

        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found with ID: " + id
                ));
    }

    private Exam findExamOrThrow(Long examId) {

        return examRepository.findById(examId)
                .filter(Exam::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The specified examination does not exist or is inactive"
                ));
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeOptionalText(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private void validatePagination(int page, int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }
    }
}