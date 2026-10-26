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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizOptionRepository optionRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;

    public QuizService(
            QuizRepository quizRepository,
            QuizQuestionRepository questionRepository,
            QuizOptionRepository optionRepository,
            CourseRepository courseRepository,
            LectureRepository lectureRepository
    ) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
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
}