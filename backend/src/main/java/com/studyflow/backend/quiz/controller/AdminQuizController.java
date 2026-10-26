package com.studyflow.backend.quiz.controller;

import com.studyflow.backend.quiz.dto.CreateQuestionRequest;
import com.studyflow.backend.quiz.dto.CreateQuizRequest;
import com.studyflow.backend.quiz.dto.QuizQuestionResponse;
import com.studyflow.backend.quiz.dto.QuizResponse;
import com.studyflow.backend.quiz.service.QuizService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/quizzes")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizController {

    private final QuizService quizService;

    public AdminQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // =========================================================
    // CREATE QUIZ
    // POST /api/v1/admin/quizzes
    // =========================================================

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(
            @Valid @RequestBody CreateQuizRequest request
    ) {
        QuizResponse response = quizService.createQuiz(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // ADD QUESTION
    // POST /api/v1/admin/quizzes/{quizId}/questions
    // =========================================================

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<QuizQuestionResponse> addQuestion(
            @PathVariable @Positive Long quizId,
            @Valid @RequestBody CreateQuestionRequest request
    ) {
        QuizQuestionResponse response =
                quizService.addQuestion(quizId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // PUBLISH QUIZ
    // PATCH /api/v1/admin/quizzes/{quizId}/publish
    // =========================================================

    @PatchMapping("/{quizId}/publish")
    public ResponseEntity<QuizResponse> publishQuiz(
            @PathVariable @Positive Long quizId
    ) {
        QuizResponse response = quizService.publishQuiz(quizId);

        return ResponseEntity.ok(response);
    }
}