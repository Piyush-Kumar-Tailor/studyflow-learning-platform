package com.studyflow.backend.dashboard.controller;

import com.studyflow.backend.dashboard.dto.StudentDashboardResponse;
import com.studyflow.backend.dashboard.service.StudentDashboardService;
import com.studyflow.backend.user.entity.AppUser;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/dashboard")
public class StudentDashboardController {

    private final StudentDashboardService dashboardService;

    public StudentDashboardController(
            StudentDashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<StudentDashboardResponse> getDashboard(
            Authentication authentication
    ) {
        AppUser user = (AppUser) authentication.getPrincipal();

        StudentDashboardResponse response =
                dashboardService.getDashboard(user.getId());

        return ResponseEntity.ok(response);
    }
}