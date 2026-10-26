package com.studyflow.backend.course.controller;

import com.studyflow.backend.course.dto.CourseRequest;
import com.studyflow.backend.course.dto.CourseResponse;
import com.studyflow.backend.course.service.CourseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/courses")
@Validated
public class AdminCourseController {

    private final CourseService courseService;

    public AdminCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // POST /api/v1/admin/courses
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(
            @Valid @RequestBody CourseRequest request
    ) {
        return courseService.createCourse(request);
    }

    // GET /api/v1/admin/courses/{id}
    @GetMapping("/{id}")
    public CourseResponse getCourseById(
            @PathVariable Long id
    ) {
        return courseService.getCourseByIdForAdmin(id);
    }

    // PUT /api/v1/admin/courses/{id}
    @PutMapping("/{id}")
    public CourseResponse updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request
    ) {
        return courseService.updateCourse(id, request);
    }

    // PATCH /api/v1/admin/courses/{id}/status
    @PatchMapping("/{id}/status")
    public CourseResponse updateCourseStatus(
            @PathVariable Long id,
            @RequestParam @NotNull Boolean active
    ) {
        return courseService.updateCourseStatus(id, active);
    }
}