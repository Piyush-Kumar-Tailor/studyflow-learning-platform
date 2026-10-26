package com.studyflow.backend.quiz.service;

import com.studyflow.backend.quiz.dto.*;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.quiz.entity.Quiz;
import com.studyflow.backend.quiz.entity.QuizAttempt;
import com.studyflow.backend.quiz.entity.QuizAttemptAnswer;
import com.studyflow.backend.quiz.entity.QuizOption;
import com.studyflow.backend.quiz.entity.QuizQuestion;
import com.studyflow.backend.quiz.enums.QuizAttemptStatus;
import com.studyflow.backend.quiz.repository.QuizAttemptAnswerRepository;
import com.studyflow.backend.quiz.repository.QuizAttemptRepository;
import com.studyflow.backend.quiz.repository.QuizOptionRepository;
import com.studyflow.backend.quiz.repository.QuizQuestionRepository;
import com.studyflow.backend.quiz.repository.QuizRepository;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class QuizAttemptService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizOptionRepository optionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAttemptAnswerRepository answerRepository;
    private final QuizService quizService;
    private final EntityManager entityManager;

    public QuizAttemptService(
            QuizRepository quizRepository,
            QuizQuestionRepository questionRepository,
            QuizOptionRepository optionRepository,
            QuizAttemptRepository attemptRepository,
            QuizAttemptAnswerRepository answerRepository,
            QuizService quizService,
            EntityManager entityManager) {

        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.quizService = quizService;
        this.entityManager = entityManager;
    }

    // --------------------------------------------------
    // 1. START QUIZ ATTEMPT
    // --------------------------------------------------

    @Transactional
    public StartQuizAttemptResponse startAttempt(Long userId, Long quizId) {

        Quiz quiz = quizRepository.findByIdAndPublishedTrue(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published quiz not found"));

        if (!quiz.getCourse().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The course is not active");
        }

        var existingAttempt = attemptRepository
                .findFirstByQuizIdAndUserIdAndStatusOrderByStartedAtDesc(
                        quizId,
                        userId,
                        QuizAttemptStatus.IN_PROGRESS
                );

        if (existingAttempt.isPresent()) {
            QuizAttempt attempt = existingAttempt.get();

            LocalDateTime expiresAt = attempt.getStartedAt()
                    .plusMinutes(attempt.getQuiz().getDurationMinutes());

            if (LocalDateTime.now().isBefore(expiresAt)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "You already have an active attempt for this quiz"
                );
            }

            attempt.setStatus(QuizAttemptStatus.EXPIRED);
            attemptRepository.save(attempt);
        }

        List<QuizQuestion> questions =
                questionRepository.findByQuizIdOrderBySequenceNumberAsc(quizId);

        if (questions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This quiz does not contain any questions");
        }

        QuizAttempt attempt = new QuizAttempt();

        attempt.setQuiz(quiz);
        attempt.setUser(entityManager.getReference(AppUser.class, userId));
        attempt.setStatus(QuizAttemptStatus.IN_PROGRESS);

        attempt = attemptRepository.saveAndFlush(attempt);

        LocalDateTime expiresAt = attempt.getStartedAt()
                .plusMinutes(quiz.getDurationMinutes());

        List<QuizQuestionResponse> questionResponses =
                quizService.getPublishedQuizQuestions(quizId);

        return new StartQuizAttemptResponse(
                attempt.getId(),
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDurationMinutes(),
                attempt.getStartedAt(),
                expiresAt,
                questionResponses
        );
    }

    // --------------------------------------------------
    // 2. SUBMIT QUIZ ATTEMPT
    // --------------------------------------------------

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public QuizResultResponse submitAttempt(
            Long userId,
            Long attemptId,
            SubmitQuizRequest request) {

        QuizAttempt attempt = attemptRepository
                .findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz attempt not found"));

        if (attempt.getStatus() != QuizAttemptStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This quiz attempt has already been submitted or expired");
        }

        Quiz quiz = attempt.getQuiz();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime expiresAt = attempt.getStartedAt()
                .plusMinutes(quiz.getDurationMinutes());

        if (!now.isBefore(expiresAt)) {
            attempt.setStatus(QuizAttemptStatus.EXPIRED);
            attemptRepository.saveAndFlush(attempt);

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The quiz time limit has expired");
        }

        List<QuizQuestion> questions =
                questionRepository.findByQuizIdOrderBySequenceNumberAsc(
                        quiz.getId());

        List<SubmitQuizRequest.AnswerRequest> submittedAnswers =
                request.answers();

        if (submittedAnswers == null
                || submittedAnswers.size() != questions.size()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Submit exactly one answer for every quiz question");
        }

        Map<Long, QuizQuestion> questionsById = new HashMap<>();

        for (QuizQuestion question : questions) {
            questionsById.put(question.getId(), question);
        }

        Set<Long> answeredQuestionIds = new HashSet<>();

        Map<Long, QuizOption> selectedOptions = new HashMap<>();

        /*
         * Validate the complete submission before saving any answers.
         * This prevents partial answer persistence if validation fails.
         */
        for (SubmitQuizRequest.AnswerRequest submitted : submittedAnswers) {

            if (submitted == null
                    || submitted.questionId() == null
                    || submitted.selectedOptionId() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Every answer must contain a question ID and option ID");
            }

            Long questionId = submitted.questionId();

            if (!answeredQuestionIds.add(questionId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate answer submitted for question ID: "
                                + questionId);
            }

            QuizQuestion question = questionsById.get(questionId);

            if (question == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Question does not belong to this quiz: "
                                + questionId);
            }

            QuizOption selectedOption = optionRepository
                    .findByIdAndQuestionId(
                            submitted.selectedOptionId(),
                            questionId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Selected option does not belong to question ID: "
                                    + questionId));

            selectedOptions.put(questionId, selectedOption);
        }

        BigDecimal score = BigDecimal.ZERO;
        BigDecimal totalMarks = BigDecimal.ZERO;

        List<QuizAttemptAnswer> savedAnswers = new ArrayList<>();

        for (QuizQuestion question : questions) {

            QuizOption selectedOption =
                    selectedOptions.get(question.getId());

            BigDecimal questionMarks =
                    BigDecimal.valueOf(question.getMarks());

            boolean correct = selectedOption.isCorrect();

            BigDecimal marksAwarded = correct
                    ? questionMarks
                    : BigDecimal.ZERO;

            totalMarks = totalMarks.add(questionMarks);
            score = score.add(marksAwarded);

            QuizAttemptAnswer answer = new QuizAttemptAnswer();

            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(selectedOption);
            answer.setCorrect(correct);
            answer.setMarksAwarded(marksAwarded);

            savedAnswers.add(answer);
        }

        if (totalMarks.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The quiz has an invalid total marks value");
        }

        BigDecimal percentage = score
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMarks, 2, RoundingMode.HALF_UP);

        boolean passed = percentage.compareTo(
                quiz.getPassingPercentage()) >= 0;

        answerRepository.saveAll(savedAnswers);

        attempt.setScore(score);
        attempt.setTotalMarks(totalMarks);
        attempt.setPercentage(percentage);
        attempt.setPassed(passed);
        attempt.setSubmittedAt(now);
        attempt.setStatus(QuizAttemptStatus.SUBMITTED);

        attemptRepository.save(attempt);

        return buildResultResponse(attempt, savedAnswers);
    }

    // --------------------------------------------------
    // 3. GET QUIZ ATTEMPT RESULT
    // --------------------------------------------------

    public QuizResultResponse getAttemptResult(
            Long userId,
            Long attemptId) {

        QuizAttempt attempt = attemptRepository
                .findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz attempt not found"));

        if (attempt.getStatus() != QuizAttemptStatus.SUBMITTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The result is not available for this attempt");
        }

        List<QuizAttemptAnswer> answers =
                answerRepository
                        .findByAttemptIdOrderByQuestionSequence(attemptId);

        return buildResultResponse(attempt, answers);
    }

    // --------------------------------------------------
    // 4. BUILD RESULT RESPONSE
    // --------------------------------------------------

    private QuizResultResponse buildResultResponse(
            QuizAttempt attempt,
            List<QuizAttemptAnswer> answers) {

        Quiz quiz = attempt.getQuiz();

        List<QuizResultResponse.AnswerResult> answerResults =
                answers.stream()
                        .map(answer -> new QuizResultResponse.AnswerResult(
                                answer.getQuestion().getId(),
                                answer.getQuestion().getQuestionText(),
                                answer.getSelectedOption() != null
                                        ? answer.getSelectedOption().getId()
                                        : null,
                                answer.getSelectedOption() != null
                                        ? answer.getSelectedOption()
                                        .getOptionText()
                                        : null,
                                answer.isCorrect(),
                                answer.getMarksAwarded(),
                                answer.getQuestion().getMarks()
                        ))
                        .toList();

        return new QuizResultResponse(
                attempt.getId(),
                quiz.getId(),
                quiz.getTitle(),
                attempt.getScore(),
                attempt.getTotalMarks(),
                attempt.getPercentage(),
                quiz.getPassingPercentage(),
                attempt.isPassed(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                answerResults
        );
    }

    public Page<QuizAttemptSummaryResponse> getMyAttempts(
            Long userId,
            int page,
            int size) {

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

        Pageable pageable = PageRequest.of(page, size);

        return attemptRepository
                .findByUserIdOrderByStartedAtDesc(userId, pageable)
                .map(attempt -> new QuizAttemptSummaryResponse(
                        attempt.getId(),
                        attempt.getQuiz().getId(),
                        attempt.getQuiz().getTitle(),
                        attempt.getScore(),
                        attempt.getTotalMarks(),
                        attempt.getPercentage(),
                        attempt.isPassed(),
                        attempt.getStatus(),
                        attempt.getStartedAt(),
                        attempt.getSubmittedAt()
                ));
    }
}