package com.studyflow.backend.user.enums;

/**
 * Defines the authorization roles available in StudyFlow.
 *
 * STUDENT:
 * A regular user who enrolls in courses, watches lectures,
 * attempts quizzes, takes mock tests, and tracks progress.
 *
 * INSTRUCTOR:
 * A user authorized to create and manage assigned courses
 * and educational content.
 *
 * ADMIN:
 * A platform administrator who manages users, courses,
 * assessments, and other operational features.
 *
 * SUPER_ADMIN:
 * A privileged administrator who manages platform-wide
 * settings, administrative access, and critical operations.
 *
 * Security note:
 * Public registration must always assign STUDENT by default.
 * Never allow users to select ADMIN or SUPER_ADMIN through
 * an ordinary registration request.
 */
public enum Role {

    STUDENT,
    INSTRUCTOR,
    ADMIN,
    SUPER_ADMIN
}