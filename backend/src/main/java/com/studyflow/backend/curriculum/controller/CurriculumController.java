package com.studyflow.backend.curriculum.controller;

import com.studyflow.backend.curriculum.dto.LectureResponse;
import com.studyflow.backend.curriculum.dto.ModuleResponse;
import com.studyflow.backend.curriculum.service.CurriculumService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    // GET /api/v1/courses/{courseId}/curriculum
    @GetMapping("/api/v1/courses/{courseId}/curriculum")
    public List<ModuleResponse> getCourseCurriculum(
            @PathVariable Long courseId
    ) {
        return curriculumService.getCourseCurriculum(courseId);
    }

    // GET /api/v1/modules/{moduleId}
    @GetMapping("/api/v1/modules/{moduleId}")
    public ModuleResponse getModule(
            @PathVariable Long moduleId
    ) {
        return curriculumService.getPublishedModule(moduleId);
    }

    // GET /api/v1/modules/{moduleId}/lectures
    @GetMapping("/api/v1/modules/{moduleId}/lectures")
    public List<LectureResponse> getModuleLectures(
            @PathVariable Long moduleId
    ) {
        return curriculumService.getModuleLectures(moduleId);
    }

    // GET /api/v1/lectures/{lectureId}
    @GetMapping("/api/v1/lectures/{lectureId}")
    public LectureResponse getLecture(
            @PathVariable Long lectureId
    ) {
        return curriculumService.getPublishedLecture(lectureId);
    }
}