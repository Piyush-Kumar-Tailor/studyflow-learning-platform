package com.studyflow.backend.admin.dto;

import java.util.List;

public record AdminLectureReorderRequest(
        List<Long> lectureIds
) {
}