package com.studyflow.backend.quiz.controller;

import com.studyflow.backend.quiz.dto.CreateQuestionRequest;
import com.studyflow.backend.quiz.dto.CreateQuizRequest;
import com.studyflow.backend.quiz.dto.QuizQuestionResponse;
import com.studyflow.backend.quiz.dto.QuizResponse;
import com.studyflow.backend.quiz.service.QuizService;
import com.studyflow.backend.quiz.dto.UpdateQuizRequest;
import com.studyflow.backend.quiz.dto.UpdateQuestionRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // GET /api/v1/admin/quizzes/{quizId}

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getQuiz(
            @PathVariable @Positive Long quizId
    ) {
        return ResponseEntity.ok(
                quizService.getQuizByIdForAdmin(quizId)
        );
    }


// GET /api/v1/admin/quizzes/{quizId}/questions

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<QuizQuestionResponse>> getQuestions(
            @PathVariable @Positive Long quizId
    ) {
        return ResponseEntity.ok(
                quizService.getQuizQuestionsForAdmin(quizId)
        );
    }


// PATCH /api/v1/admin/quizzes/{quizId}/unpublish

    @PatchMapping("/{quizId}/unpublish")
    public ResponseEntity<QuizResponse> unpublishQuiz(
            @PathVariable @Positive Long quizId
    ) {
        return ResponseEntity.ok(
                quizService.unpublishQuiz(quizId)
        );
    }

    // =========================================================
// UPDATE QUIZ DETAILS
// PUT /api/v1/admin/quizzes/{quizId}
// =========================================================

    @PutMapping("/{quizId}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable @Positive Long quizId,
            @Valid @RequestBody UpdateQuizRequest request
    ) {
        return ResponseEntity.ok(
                quizService.updateQuiz(quizId, request)
        );
    }


// =========================================================
// UPDATE QUESTION AND OPTIONS
// PUT /api/v1/admin/quizzes/{quizId}/questions/{questionId}
// =========================================================

    @PutMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<QuizQuestionResponse> updateQuestion(
            @PathVariable @Positive Long quizId,
            @PathVariable @Positive Long questionId,
            @Valid @RequestBody UpdateQuestionRequest request
    ) {
        return ResponseEntity.ok(
                quizService.updateQuestion(
                        quizId,
                        questionId,
                        request
                )
        );
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId
    ) {
        quizService.deleteQuestion(quizId, questionId);
        return ResponseEntity.noContent().build();
    }
}