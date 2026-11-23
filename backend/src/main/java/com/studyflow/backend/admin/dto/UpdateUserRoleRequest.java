
package com.studyflow.backend.admin.dto;

import jakarta.validation.constraints.NotNull;

import com.studyflow.backend.user.enums.Role;

public record UpdateUserRoleRequest(
        @NotNull Role role
) {
}
