package com.studyflow.backend.quiz.controller;

import com.studyflow.backend.quiz.dto.QuizAttemptSummaryResponse;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.quiz.dto.QuizResultResponse;
import com.studyflow.backend.quiz.dto.StartQuizAttemptResponse;
import com.studyflow.backend.quiz.dto.SubmitQuizRequest;
import com.studyflow.backend.quiz.service.QuizAttemptService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quiz-attempts")
@Validated
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    // -----------------------------------------
    // 1. START A QUIZ
    // -----------------------------------------

    @PostMapping("/quizzes/{quizId}/start")
    @ResponseStatus(HttpStatus.CREATED)
    public StartQuizAttemptResponse startAttempt(
            @PathVariable @Positive Long quizId,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        return quizAttemptService.startAttempt(
                user.getId(),
                quizId
        );
    }

    // -----------------------------------------
    // 2. SUBMIT QUIZ ANSWERS
    // -----------------------------------------

    @PostMapping("/{attemptId}/submit")
    public QuizResultResponse submitAttempt(
            @PathVariable @Positive Long attemptId,
            @Valid @RequestBody SubmitQuizRequest request,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        return quizAttemptService.submitAttempt(
                user.getId(),
                attemptId,
                request
        );
    }

    // -----------------------------------------
    // 3. GET QUIZ RESULT
    // -----------------------------------------

    @GetMapping("/{attemptId}/result")
    public QuizResultResponse getAttemptResult(
            @PathVariable @Positive Long attemptId,
            Authentication authentication) {

        AppUser user = getAuthenticatedUser(authentication);

        return quizAttemptService.getAttemptResult(
                user.getId(),
                attemptId
        );
    }

    // -----------------------------------------
    // AUTHENTICATED USER
    // -----------------------------------------

    private AppUser getAuthenticatedUser(Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof AppUser user)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "Authenticated user information is unavailable");
        }

        return user;
    }

    // -----------------------------------------
// 4. GET MY QUIZ ATTEMPT HISTORY
// -----------------------------------------

    @GetMapping("/my")
    public Page<QuizAttemptSummaryResponse> getMyAttempts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        AppUser user = getAuthenticatedUser(authentication);

        return quizAttemptService.getMyAttempts(
                user.getId(),
                page,
                size
        );
    }
}