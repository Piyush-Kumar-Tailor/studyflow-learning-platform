package com.studyflow.backend.auth.dto;

public record AuthResponse(
        Long id,
        String name,
        String email,
        String role,
        String accessToken,
        String message
) {
}