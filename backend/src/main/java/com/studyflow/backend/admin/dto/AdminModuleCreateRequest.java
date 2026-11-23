package com.studyflow.backend.admin.dto;

public record AdminModuleCreateRequest(
        String title,
        String description,
        Integer sequenceNumber,
        Boolean published
) {
}