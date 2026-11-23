package com.studyflow.backend.user.controller;

import com.studyflow.backend.user.dto.UpdateProfileRequest;
import com.studyflow.backend.user.dto.UserProfileResponse;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.user.service.UserProfileService;
import com.studyflow.backend.user.dto.CurrentUserResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/me")
public class UserController {

    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile(
            Authentication authentication
    ) {
        AppUser user = getAuthenticatedUser(authentication);

        return userProfileService.getProfile(user);
    }

    @PutMapping("/profile")
    public UserProfileResponse updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        AppUser user = getAuthenticatedUser(authentication);

        return userProfileService.updateProfile(user, request);
    }

    @GetMapping
    public CurrentUserResponse getCurrentUser(
            Authentication authentication
    ) {
        AppUser user = getAuthenticatedUser(authentication);

        return CurrentUserResponse.from(user);
    }

    private AppUser getAuthenticatedUser(
            Authentication authentication
    ) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof AppUser user)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required."
            );
        }

        return user;
    }
}