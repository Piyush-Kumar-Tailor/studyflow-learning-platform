package com.studyflow.backend.quiz.service;

import com.studyflow.backend.course.entity.Course;
import com.studyflow.backend.course.repository.CourseRepository;
import com.studyflow.backend.curriculum.entity.Lecture;
import com.studyflow.backend.curriculum.repository.LectureRepository;
import com.studyflow.backend.quiz.dto.CreateQuestionRequest;
import com.studyflow.backend.quiz.dto.CreateQuizRequest;
import com.studyflow.backend.quiz.dto.QuizQuestionResponse;
import com.studyflow.backend.quiz.dto.QuizResponse;
import com.studyflow.backend.quiz.entity.Quiz;
import com.studyflow.backend.quiz.entity.QuizOption;
import com.studyflow.backend.quiz.entity.QuizQuestion;
import com.studyflow.backend.quiz.repository.QuizOptionRepository;
import com.studyflow.backend.quiz.repository.QuizQuestionRepository;
import com.studyflow.backend.quiz.repository.QuizRepository;
import com.studyflow.backend.quiz.dto.UpdateQuizRequest;
import com.studyflow.backend.quiz.dto.UpdateQuestionRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.studyflow.backend.quiz.repository.QuizAttemptRepository;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizOptionRepository optionRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final QuizAttemptRepository attemptRepository;

    public QuizService(
            QuizRepository quizRepository,
            QuizQuestionRepository questionRepository,
            QuizOptionRepository optionRepository,
            CourseRepository courseRepository,
            LectureRepository lectureRepository,
            QuizAttemptRepository attemptRepository
    ) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.attemptRepository = attemptRepository;
    }

    // =========================================================
    // CREATE QUIZ
    // =========================================================

    @Transactional
    public QuizResponse createQuiz(CreateQuizRequest request) {

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        if (!course.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create a quiz for an inactive course"
            );
        }

        Lecture lecture = null;

        if (request.lectureId() != null) {
            lecture = lectureRepository
                    .findByIdAndPublishedTrue(request.lectureId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Published lecture not found"
                    ));

            if (!lecture.getModule().isPublished()
                    || !lecture.getModule().getCourse().getId()
                    .equals(course.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Lecture must be published and belong to the selected course"
                );
            }
        }

        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setLecture(lecture);
        quiz.setTitle(request.title().trim());
        quiz.setDescription(request.description());
        quiz.setDurationMinutes(request.durationMinutes());
        quiz.setPassingPercentage(request.passingPercentage());
        quiz.setPublished(false);

        Quiz savedQuiz = quizRepository.save(quiz);

        return toQuizResponse(savedQuiz);
    }

    // =========================================================
    // ADD QUESTION
    // =========================================================

    @Transactional
    public QuizQuestionResponse addQuestion(
            Long quizId,
            CreateQuestionRequest request
    ) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        if (quiz.isPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Questions cannot be added after a quiz is published"
            );
        }

        long correctOptions = request.options()
                .stream()
                .filter(option -> Boolean.TRUE.equals(option.correct()))
                .count();

        if (correctOptions != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Each question must have exactly one correct option"
            );
        }

        if (questionRepository.existsByQuizIdAndSequenceNumber(
                quizId,
                request.sequenceNumber()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Question sequence number already exists in this quiz"
            );
        }

        QuizQuestion question = new QuizQuestion();
        question.setQuiz(quiz);
        question.setQuestionText(request.questionText().trim());
        question.setSequenceNumber(request.sequenceNumber());
        question.setMarks(request.marks());

        QuizQuestion savedQuestion = questionRepository.save(question);

        List<QuizOption> options = request.options()
                .stream()
                .map(optionRequest -> {
                    QuizOption option = new QuizOption();
                    option.setQuestion(savedQuestion);
                    option.setOptionText(optionRequest.optionText().trim());
                    option.setCorrect(optionRequest.correct());
                    return option;
                })
                .toList();

        optionRepository.saveAll(options);

        return toQuestionResponse(savedQuestion, options);
    }

    // =========================================================
    // PUBLISH QUIZ
    // =========================================================

    @Transactional
    public QuizResponse publishQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        if (quiz.isPublished()) {
            return toQuizResponse(quiz);
        }

        List<QuizQuestion> questions =
                questionRepository.findByQuizIdOrderBySequenceNumberAsc(
                        quizId
                );

        if (questions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quiz must contain at least one question before publishing"
            );
        }

        for (QuizQuestion question : questions) {

            long optionCount =
                    optionRepository.countByQuestionId(question.getId());

            long correctOptionCount =
                    optionRepository.countByQuestionIdAndCorrectTrue(
                            question.getId()
                    );

            if (optionCount < 2 || correctOptionCount != 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Every question must have at least two options and exactly one correct option"
                );
            }
        }

        quiz.setPublished(true);

        Quiz savedQuiz = quizRepository.save(quiz);

        return toQuizResponse(savedQuiz);
    }

    // =========================================================
    // GET PUBLISHED QUIZZES FOR A COURSE
    // =========================================================

    public Page<QuizResponse> getPublishedQuizzes(
            Long courseId,
            Pageable pageable
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        if (!course.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            );
        }

        return quizRepository
                .findByCourseIdAndPublishedTrue(courseId, pageable)
                .map(this::toQuizResponse);
    }

    // =========================================================
    // GET PUBLISHED QUIZ DETAILS
    // =========================================================

    public QuizResponse getPublishedQuiz(Long quizId) {

        Quiz quiz = quizRepository.findByIdAndPublishedTrue(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published quiz not found"
                ));

        if (!quiz.getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Published quiz not found"
            );
        }

        return toQuizResponse(quiz);
    }

    // =========================================================
    // GET PUBLISHED QUIZ QUESTIONS
    // =========================================================

    public List<QuizQuestionResponse> getPublishedQuizQuestions(
            Long quizId
    ) {
        Quiz quiz = quizRepository.findByIdAndPublishedTrue(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published quiz not found"
                ));

        if (!quiz.getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Published quiz not found"
            );
        }

        return questionRepository
                .findByQuizIdOrderBySequenceNumberAsc(quizId)
                .stream()
                .map(question -> {
                    List<QuizOption> options =
                            optionRepository
                                    .findByQuestionIdOrderByIdAsc(
                                            question.getId()
                                    );

                    return toQuestionResponse(question, options);
                })
                .toList();
    }


    // =========================================================
    // ADMIN: GET QUIZ DETAILS
    // =========================================================

    public QuizResponse getQuizByIdForAdmin(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        return toQuizResponse(quiz);
    }


    // =========================================================
    // ADMIN: GET ALL QUESTIONS INCLUDING UNPUBLISHED QUIZ
    // =========================================================

    public List<QuizQuestionResponse> getQuizQuestionsForAdmin(
            Long quizId
    ) {

        if (!quizRepository.existsById(quizId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Quiz not found"
            );
        }

        return questionRepository
                .findByQuizIdOrderBySequenceNumberAsc(quizId)
                .stream()
                .map(question -> {
                    List<QuizOption> options =
                            optionRepository.findByQuestionIdOrderByIdAsc(
                                    question.getId()
                            );

                    return toQuestionResponse(question, options);
                })
                .toList();
    }


    // =========================================================
    // ADMIN: UNPUBLISH QUIZ
    // =========================================================

    @Transactional
    public QuizResponse unpublishQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        if (!quiz.isPublished()) {
            return toQuizResponse(quiz);
        }

        // Protect existing assessment history.
        if (attemptRepository.existsByQuizId(quizId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot unpublish this quiz because student "
                            + "attempts already exist."
            );
        }

        quiz.setPublished(false);

        return toQuizResponse(quizRepository.save(quiz));
    }

    // =========================================================
// ADMIN: UPDATE QUIZ DETAILS
// =========================================================

    @Transactional
    public QuizResponse updateQuiz(
            Long quizId,
            UpdateQuizRequest request
    ) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        validateQuizEditable(quiz);

        quiz.setTitle(request.title().trim());
        quiz.setDescription(normalizeOptionalText(request.description()));
        quiz.setDurationMinutes(request.durationMinutes());
        quiz.setPassingPercentage(request.passingPercentage());

        return toQuizResponse(quizRepository.save(quiz));
    }

    // =========================================================
// ADMIN: UPDATE QUESTION AND OPTIONS
// =========================================================

    @Transactional
    public QuizQuestionResponse updateQuestion(
            Long quizId,
            Long questionId,
            UpdateQuestionRequest request
    ) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        validateQuizEditable(quiz);

        QuizQuestion question = questionRepository
                .findByIdAndQuizId(questionId, quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Question not found in this quiz"
                ));

        long correctOptions = request.options()
                .stream()
                .filter(option -> Boolean.TRUE.equals(option.correct()))
                .count();

        if (correctOptions != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Each question must have exactly one correct option"
            );
        }

        boolean sequenceExists =
                questionRepository
                        .existsByQuizIdAndSequenceNumberAndIdNot(
                                quizId,
                                request.sequenceNumber(),
                                questionId
                        );

        if (sequenceExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another question already uses this sequence number"
            );
        }

        question.setQuestionText(request.questionText().trim());
        question.setSequenceNumber(request.sequenceNumber());
        question.setMarks(request.marks());

        QuizQuestion savedQuestion =
                questionRepository.save(question);

        // Existing options can be replaced because this quiz
        // has no student attempts.
        List<QuizOption> existingOptions =
                optionRepository.findByQuestionId(questionId);

        optionRepository.deleteAll(existingOptions);
        optionRepository.flush();

        List<QuizOption> updatedOptions = request.options()
                .stream()
                .map(optionRequest -> {
                    QuizOption option = new QuizOption();
                    option.setQuestion(savedQuestion);
                    option.setOptionText(
                            optionRequest.optionText().trim()
                    );
                    option.setCorrect(optionRequest.correct());
                    return option;
                })
                .toList();

        List<QuizOption> savedOptions =
                optionRepository.saveAll(updatedOptions);

        return toQuestionResponse(savedQuestion, savedOptions);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found with id: " + quizId
                ));

        if (attemptRepository.existsByQuizId(quizId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a quiz after student attempts exist"
            );
        }

        quizRepository.delete(quiz);
    }

    @Transactional
    public void deleteQuestion(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found with id: " + quizId
                ));

        if (attemptRepository.existsByQuizId(quizId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete questions after student attempts exist"
            );
        }

        QuizQuestion question = questionRepository
                .findByIdAndQuizId(questionId, quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Question not found with id: " + questionId
                ));

        optionRepository.deleteAll(
                optionRepository.findByQuestionId(questionId)
        );

        questionRepository.delete(question);
    }


    // =========================================================
    // DTO MAPPERS
    // =========================================================

    private QuizResponse toQuizResponse(Quiz quiz) {

        return new QuizResponse(
                quiz.getId(),
                quiz.getCourse().getId(),
                quiz.getLecture() != null
                        ? quiz.getLecture().getId()
                        : null,
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getPassingPercentage(),
                quiz.isPublished(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt()
        );
    }

    private QuizQuestionResponse toQuestionResponse(
            QuizQuestion question,
            List<QuizOption> options
    ) {
        List<QuizQuestionResponse.OptionResponse> optionResponses =
                options.stream()
                        .map(option ->
                                new QuizQuestionResponse.OptionResponse(
                                        option.getId(),
                                        option.getOptionText()
                                )
                        )
                        .toList();

        return new QuizQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getSequenceNumber(),
                question.getMarks(),
                optionResponses
        );
    }

    private void validateQuizEditable(Quiz quiz) {

        if (quiz.isPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unpublish the quiz before editing it"
            );
        }

        if (attemptRepository.existsByQuizId(quiz.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot modify a quiz after student attempts exist"
            );
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}