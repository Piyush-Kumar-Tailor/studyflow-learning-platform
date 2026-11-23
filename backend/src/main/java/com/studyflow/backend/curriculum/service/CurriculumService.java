package com.studyflow.backend.curriculum.service;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.course.repository.CourseRepository;
import com.studyflow.backend.curriculum.dto.LectureRequest;
import com.studyflow.backend.curriculum.dto.LectureResponse;
import com.studyflow.backend.curriculum.dto.ModuleRequest;
import com.studyflow.backend.curriculum.dto.ModuleResponse;
import com.studyflow.backend.curriculum.entity.CourseModule;
import com.studyflow.backend.curriculum.entity.Lecture;
import com.studyflow.backend.curriculum.repository.CourseModuleRepository;

import com.studyflow.backend.curriculum.repository.LectureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.studyflow.backend.enrollment.repository.LectureProgressRepository;
import com.studyflow.backend.quiz.repository.QuizRepository;
import com.studyflow.backend.quiz.repository.QuizAttemptRepository;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CurriculumService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LectureRepository lectureRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;


    public CurriculumService(
            CourseRepository courseRepository,
            CourseModuleRepository moduleRepository,
            LectureRepository lectureRepository,
            LectureProgressRepository lectureProgressRepository,
            QuizRepository quizRepository,
            QuizAttemptRepository quizAttemptRepository
    ) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lectureRepository = lectureRepository;
        this.lectureProgressRepository = lectureProgressRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    // =========================================================
    // PUBLIC: GET COMPLETE COURSE CURRICULUM
    // =========================================================

    public List<ModuleResponse> getCourseCurriculum(Long courseId) {

        Course course = findActiveCourseOrThrow(courseId);

        return moduleRepository
                .findByCourseIdAndPublishedTrueOrderBySequenceNumberAsc(
                        course.getId()
                )
                .stream()
                .map(ModuleResponse::from)
                .toList();
    }

    // =========================================================
    // PUBLIC: GET PUBLISHED LECTURES FOR A MODULE
    // =========================================================

    public List<LectureResponse> getModuleLectures(Long moduleId) {

        CourseModule module = findPublishedModuleOrThrow(moduleId);

        return lectureRepository
                .findByModuleIdAndPublishedTrueOrderBySequenceNumberAsc(
                        module.getId()
                )
                .stream()
                .map(LectureResponse::from)
                .toList();
    }

    // =========================================================
    // PUBLIC: GET A SINGLE PUBLISHED MODULE
    // =========================================================

    public ModuleResponse getPublishedModule(Long moduleId) {

        return ModuleResponse.from(
                findPublishedModuleOrThrow(moduleId)
        );
    }

    // =========================================================
    // PUBLIC: GET A SINGLE PUBLISHED LECTURE
    // =========================================================

    public LectureResponse getPublishedLecture(Long lectureId) {

        return LectureResponse.from(
                findPublishedLectureOrThrow(lectureId)
        );
    }

    // =========================================================
    // ADMIN: CREATE MODULE
    // =========================================================

    @Transactional
    public ModuleResponse createModule(ModuleRequest request) {

        Course course = findActiveCourseOrThrow(request.courseId());

        boolean sequenceExists =
                moduleRepository.existsByCourseIdAndSequenceNumber(
                        course.getId(),
                        request.sequenceNumber()
                );

        if (sequenceExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A module already exists at this sequence number"
            );
        }

        CourseModule module = new CourseModule();

        module.setCourse(course);
        module.setTitle(request.title().trim());
        module.setDescription(
                normalizeOptionalText(request.description())
        );
        module.setSequenceNumber(request.sequenceNumber());
        module.setPublished(false);

        return ModuleResponse.from(
                moduleRepository.save(module)
        );
    }

    // =========================================================
    // ADMIN: UPDATE MODULE
    // =========================================================

    @Transactional
    public ModuleResponse updateModule(
            Long moduleId,
            ModuleRequest request
    ) {

        CourseModule module = findModuleOrThrow(moduleId);

        Course course = findActiveCourseOrThrow(request.courseId());

        boolean sequenceExists =
                moduleRepository
                        .existsByCourseIdAndSequenceNumberAndIdNot(
                                course.getId(),
                                request.sequenceNumber(),
                                moduleId
                        );

        if (sequenceExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another module already uses this sequence number"
            );
        }

        module.setCourse(course);
        module.setTitle(request.title().trim());
        module.setDescription(
                normalizeOptionalText(request.description())
        );
        module.setSequenceNumber(request.sequenceNumber());

        return ModuleResponse.from(
                moduleRepository.save(module)
        );
    }

    // =========================================================
    // ADMIN: CREATE LECTURE
    // =========================================================

    @Transactional
    public LectureResponse createLecture(LectureRequest request) {

        CourseModule module = findModuleOrThrow(request.moduleId());

        if (!module.getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot add a lecture to a course that is inactive"
            );
        }

        boolean sequenceExists =
                lectureRepository.existsByModuleIdAndSequenceNumber(
                        module.getId(),
                        request.sequenceNumber()
                );

        if (sequenceExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A lecture already exists at this sequence number"
            );
        }

        Lecture lecture = new Lecture();

        lecture.setModule(module);
        lecture.setTitle(request.title().trim());
        lecture.setDescription(
                normalizeOptionalText(request.description())
        );
        lecture.setYoutubeVideoId(
                request.youtubeVideoId().trim()
        );
        lecture.setDurationSeconds(request.durationSeconds());
        lecture.setSequenceNumber(request.sequenceNumber());
        lecture.setPublished(false);

        return LectureResponse.from(
                lectureRepository.save(lecture)
        );
    }

    // =========================================================
    // ADMIN: UPDATE LECTURE
    // =========================================================

    @Transactional
    public LectureResponse updateLecture(
            Long lectureId,
            LectureRequest request
    ) {

        Lecture lecture = findLectureOrThrow(lectureId);

        CourseModule module = findModuleOrThrow(request.moduleId());

        if (!module.getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot move a lecture to an inactive course"
            );
        }

        boolean sequenceExists =
                lectureRepository
                        .existsByModuleIdAndSequenceNumberAndIdNot(
                                module.getId(),
                                request.sequenceNumber(),
                                lectureId
                        );

        if (sequenceExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another lecture already uses this sequence number"
            );
        }

        lecture.setModule(module);
        lecture.setTitle(request.title().trim());
        lecture.setDescription(
                normalizeOptionalText(request.description())
        );
        lecture.setYoutubeVideoId(
                request.youtubeVideoId().trim()
        );
        lecture.setDurationSeconds(request.durationSeconds());
        lecture.setSequenceNumber(request.sequenceNumber());

        return LectureResponse.from(
                lectureRepository.save(lecture)
        );
    }

    // =========================================================
    // ADMIN: PUBLISH OR UNPUBLISH MODULE
    // =========================================================

    @Transactional
    public ModuleResponse updateModulePublishedStatus(
            Long moduleId,
            boolean published
    ) {

        CourseModule module = findModuleOrThrow(moduleId);

        if (published && !module.getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot publish a module belonging to an inactive course"
            );
        }

        module.setPublished(published);

        return ModuleResponse.from(
                moduleRepository.save(module)
        );
    }

    // =========================================================
    // ADMIN: PUBLISH OR UNPUBLISH LECTURE
    // =========================================================

    @Transactional
    public LectureResponse updateLecturePublishedStatus(
            Long lectureId,
            boolean published
    ) {

        Lecture lecture = findLectureOrThrow(lectureId);

        if (published && !lecture.getModule().isPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Publish the module before publishing its lecture"
            );
        }

        if (published && !lecture.getModule().getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot publish a lecture belonging to an inactive course"
            );
        }

        lecture.setPublished(published);

        return LectureResponse.from(
                lectureRepository.save(lecture)
        );
    }

    // =========================================================
// ADMIN: DELETE EMPTY MODULE
// =========================================================

    @Transactional
    public void deleteModule(Long moduleId) {

        CourseModule module = findModuleOrThrow(moduleId);

        boolean hasLectures =
                lectureRepository.existsByModuleId(moduleId);

        if (hasLectures) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a module containing lectures. "
                            + "Remove or manage its lectures first."
            );
        }

        moduleRepository.delete(module);
    }


// =========================================================
// ADMIN: DELETE UNUSED LECTURE
// =========================================================

    @Transactional
    public void deleteLecture(Long lectureId) {

        Lecture lecture = findLectureOrThrow(lectureId);

        // Check whether students have learning progress.
        if (lectureProgressRepository.existsByLectureId(lectureId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this lecture because student "
                            + "learning progress already exists."
            );
        }

        // Check whether quizzes are associated with this lecture.
        if (quizRepository.existsByLectureId(lectureId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this lecture because quizzes "
                            + "are associated with it."
            );
        }

        lectureRepository.delete(lecture);
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private Course findActiveCourseOrThrow(Long courseId) {

        return courseRepository.findById(courseId)
                .filter(Course::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active course not found with ID: " + courseId
                ));
    }

    private CourseModule findModuleOrThrow(Long moduleId) {

        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Module not found with ID: " + moduleId
                ));
    }

    private CourseModule findPublishedModuleOrThrow(Long moduleId) {

        CourseModule module = findModuleOrThrow(moduleId);

        if (!module.isPublished() || !module.getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Published module not found"
            );
        }

        return module;
    }

    private Lecture findLectureOrThrow(Long lectureId) {

        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lecture not found with ID: " + lectureId
                ));
    }

    private Lecture findPublishedLectureOrThrow(Long lectureId) {

        Lecture lecture = findLectureOrThrow(lectureId);

        if (!lecture.isPublished()
                || !lecture.getModule().isPublished()
                || !lecture.getModule().getCourse().isActive()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Published lecture not found"
            );
        }

        return lecture;
    }

    private String normalizeOptionalText(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}