package com.studyflow.backend.user.dto;

import com.studyflow.backend.user.entity.AppUser;

import java.time.LocalDateTime;

public record CurrentUserResponse(
        Long id,
        String name,
        String email,
        String role,
        LocalDateTime createdAt
) {

    public static CurrentUserResponse from(AppUser user) {
        return new CurrentUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}