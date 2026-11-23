package com.studyflow.backend.admin.dto;

import java.util.List;

public record AdminDashboardOverviewResponse(

        AdminDashboardResponse statistics,

        List<RecentStudentResponse> recentStudents,

        List<PopularCourseResponse> popularCourses

) {
}