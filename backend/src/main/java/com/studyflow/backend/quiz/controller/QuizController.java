package com.studyflow.backend.quiz.controller;

import com.studyflow.backend.quiz.dto.QuizQuestionResponse;
import com.studyflow.backend.quiz.dto.QuizResponse;
import com.studyflow.backend.quiz.service.QuizService;

import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@Validated
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // =========================================================
    // GET PUBLISHED QUIZZES FOR A COURSE
    // GET /api/v1/quizzes?courseId=1&page=0&size=10
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<QuizResponse>> getPublishedQuizzes(
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be zero or greater")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            int size
    ) {
        if (size > 100) {
            size = 100;
        }

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                quizService.getPublishedQuizzes(courseId, pageable)
        );
    }

    // =========================================================
    // GET PUBLISHED QUIZ DETAILS
    // GET /api/v1/quizzes/{quizId}
    // =========================================================

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getPublishedQuiz(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(
                quizService.getPublishedQuiz(quizId)
        );
    }

    // =========================================================
    // GET QUESTIONS AND OPTIONS
    // GET /api/v1/quizzes/{quizId}/questions
    // =========================================================

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<QuizQuestionResponse>> getQuizQuestions(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(
                quizService.getPublishedQuizQuestions(quizId)
        );
    }
}