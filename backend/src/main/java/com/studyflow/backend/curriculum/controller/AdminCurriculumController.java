package com.studyflow.backend.curriculum.controller;

import com.studyflow.backend.curriculum.dto.LectureRequest;
import com.studyflow.backend.curriculum.dto.LectureResponse;
import com.studyflow.backend.curriculum.dto.ModuleRequest;
import com.studyflow.backend.curriculum.dto.ModuleResponse;
import com.studyflow.backend.curriculum.dto.UpdatePublishedRequest;
import com.studyflow.backend.curriculum.service.CurriculumService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminCurriculumController {

    private final CurriculumService curriculumService;

    public AdminCurriculumController(
            CurriculumService curriculumService
    ) {
        this.curriculumService = curriculumService;
    }

    // POST /api/v1/admin/modules
    @PostMapping("/modules")
    @ResponseStatus(HttpStatus.CREATED)
    public ModuleResponse createModule(
            @Valid @RequestBody ModuleRequest request
    ) {
        return curriculumService.createModule(request);
    }

    // PUT /api/v1/admin/modules/{id}
    @PutMapping("/modules/{id}")
    public ModuleResponse updateModule(
            @PathVariable Long id,
            @Valid @RequestBody ModuleRequest request
    ) {
        return curriculumService.updateModule(id, request);
    }

    // PATCH /api/v1/admin/modules/{id}/publication
    @PatchMapping("/modules/{id}/publication")
    public ModuleResponse updateModulePublication(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePublishedRequest request
    ) {
        return curriculumService.updateModulePublishedStatus(
                id,
                request.published()
        );
    }

    // POST /api/v1/admin/lectures
    @PostMapping("/lectures")
    @ResponseStatus(HttpStatus.CREATED)
    public LectureResponse createLecture(
            @Valid @RequestBody LectureRequest request
    ) {
        return curriculumService.createLecture(request);
    }

    // PUT /api/v1/admin/lectures/{id}
    @PutMapping("/lectures/{id}")
    public LectureResponse updateLecture(
            @PathVariable Long id,
            @Valid @RequestBody LectureRequest request
    ) {
        return curriculumService.updateLecture(id, request);
    }

    // PATCH /api/v1/admin/lectures/{id}/publication
    @PatchMapping("/lectures/{id}/publication")
    public LectureResponse updateLecturePublication(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePublishedRequest request
    ) {
        return curriculumService.updateLecturePublishedStatus(
                id,
                request.published()
        );
    }
}