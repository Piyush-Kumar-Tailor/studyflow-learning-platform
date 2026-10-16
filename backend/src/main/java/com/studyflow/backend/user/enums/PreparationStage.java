package com.studyflow.backend.user.enums;

/**
 * Represents a user's preparation stage for an examination
 * or structured learning goal.
 *
 * BEGINNER:
 * The user is starting preparation or learning the subject
 * for the first time.
 *
 * INTERMEDIATE:
 * The user already understands some concepts and has begun
 * practicing questions or following a study plan.
 *
 * REPEATER:
 * The user is preparing again after a previous preparation
 * cycle or examination attempt.
 *
 * TEST_SERIES:
 * The user primarily wants practice tests, mock examinations,
 * performance analysis, and targeted revision.
 *
 * Product note:
 * This describes preparation status, not the user's actual
 * knowledge level or guaranteed examination readiness.
 */
public enum PreparationStage {

    BEGINNER,
    INTERMEDIATE,
    REPEATER,
    TEST_SERIES
}