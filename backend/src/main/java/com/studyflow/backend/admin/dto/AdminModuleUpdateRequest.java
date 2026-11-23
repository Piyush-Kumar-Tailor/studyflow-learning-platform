package com.studyflow.backend.admin.dto;

public record AdminModuleUpdateRequest(
        String title,
        String description,
        Integer sequenceNumber,
        Boolean published
) {
}