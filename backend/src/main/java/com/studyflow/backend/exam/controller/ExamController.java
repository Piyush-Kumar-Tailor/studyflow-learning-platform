package com.studyflow.backend.exam.controller;

import com.studyflow.backend.exam.dto.ExamResponse;
import com.studyflow.backend.exam.service.ExamService;
import com.studyflow.backend.user.enums.ExamCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    // GET /api/v1/exams
    @GetMapping
    public Page<ExamResponse> getExams(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ExamCategory category,
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @RequestParam(defaultValue = "10")
            @Min(1) @Max(100) int size
    ) {
        return examService.getExams(search, category, page, size);
    }

    // GET /api/v1/exams/{id}
    @GetMapping("/{id}")
    public ExamResponse getExamById(@PathVariable Long id) {
        return examService.getExamById(id);
    }
}