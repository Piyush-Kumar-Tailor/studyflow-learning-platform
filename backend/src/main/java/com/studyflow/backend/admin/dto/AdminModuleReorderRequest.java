package com.studyflow.backend.admin.dto;

import java.util.List;

public record AdminModuleReorderRequest(
        List<Long> moduleIds
) {
}