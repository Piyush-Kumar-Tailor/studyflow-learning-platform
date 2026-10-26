package com.studyflow.backend.exam.controller;

import com.studyflow.backend.exam.dto.ExamRequest;
import com.studyflow.backend.exam.dto.ExamResponse;
import com.studyflow.backend.exam.dto.ExamStatusRequest;
import com.studyflow.backend.exam.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/exams")
public class AdminExamController {

    private final ExamService examService;

    public AdminExamController(ExamService examService) {
        this.examService = examService;
    }

    // POST /api/v1/admin/exams
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamResponse createExam(
            @Valid @RequestBody ExamRequest request
    ) {
        return examService.createExam(request);
    }

    // GET /api/v1/admin/exams/{id}
    @GetMapping("/{id}")
    public ExamResponse getExamById(@PathVariable Long id) {
        return examService.getExamByIdForAdmin(id);
    }

    // PUT /api/v1/admin/exams/{id}
    @PutMapping("/{id}")
    public ExamResponse updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request
    ) {
        return examService.updateExam(id, request);
    }

    // PATCH /api/v1/admin/exams/{id}/status
    @PatchMapping("/{id}/status")
    public ExamResponse updateExamStatus(
            @PathVariable Long id,
            @Valid @RequestBody ExamStatusRequest request
    ) {
        return examService.updateExamStatus(id, request);
    }
}