package com.studyflow.backend.course.controller;

import com.studyflow.backend.course.dto.CourseResponse;
import com.studyflow.backend.course.service.CourseService;
import com.studyflow.backend.user.enums.LanguagePreference;
import com.studyflow.backend.user.enums.SkillLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@Validated
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // GET /api/v1/courses
    @GetMapping
    public Page<CourseResponse> getCourses(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SkillLevel difficulty,
            @RequestParam(required = false) LanguagePreference language,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {

        return courseService.getCourses(
                examId,
                search,
                difficulty,
                language,
                page,
                size
        );
    }

    // GET /api/v1/courses/{slug}
    @GetMapping("/{slug}")
    public CourseResponse getCourseBySlug(
            @PathVariable String slug
    ) {
        return courseService.getCourseBySlug(slug);
    }
}