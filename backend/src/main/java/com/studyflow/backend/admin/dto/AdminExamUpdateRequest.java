package com.studyflow.backend.admin.dto;

public record AdminExamUpdateRequest(
        String name,
        String code,
        String category,
        String conductingBody,
        String description,
        String officialWebsite
) {
}