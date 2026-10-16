package com.studyflow.backend.user.enums;

/**
 * Represents a preferred learning resource or course format.
 *
 * VIDEO_LECTURES:
 * Courses primarily organized around video lessons.
 *
 * VIDEO_WITH_QUIZZES:
 * Video lessons accompanied by quizzes or knowledge checks.
 *
 * FULL_COURSE_WITH_TESTS:
 * Structured courses containing lessons, assessments,
 * and course-level tests.
 *
 * PRACTICE_QUESTIONS:
 * Collections of practice questions organized by topic.
 *
 * MOCK_TESTS:
 * Timed or structured mock examinations with result analysis.
 *
 * REVISION_NOTES:
 * Concise study notes intended for revision.
 *
 * FLASHCARDS:
 * Question-and-answer cards for active recall and revision.
 *
 * PROJECT_BASED_LEARNING:
 * Learning organized around practical projects and assignments.
 *
 * INTERVIEW_PREPARATION:
 * Learning resources focused on interviews and job readiness.
 *
 * Product note:
 * Users should be able to select multiple formats.
 * Availability depends on the actual resources in the catalog.
 */
public enum CourseFormatPreference {

    VIDEO_LECTURES,
    VIDEO_WITH_QUIZZES,
    FULL_COURSE_WITH_TESTS,
    PRACTICE_QUESTIONS,
    MOCK_TESTS,
    REVISION_NOTES,
    FLASHCARDS,
    PROJECT_BASED_LEARNING,
    INTERVIEW_PREPARATION
}