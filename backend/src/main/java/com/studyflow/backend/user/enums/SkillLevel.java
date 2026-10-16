package com.studyflow.backend.user.enums;

/**
 * Represents a learner's current skill or knowledge level.
 *
 * COMPLETE_BEGINNER:
 * The user has little or no prior knowledge of the subject.
 *
 * BEGINNER:
 * The user understands basic concepts but needs structured
 * instruction and guided practice.
 *
 * INTERMEDIATE:
 * The user understands core concepts and can solve common
 * problems with some independence.
 *
 * ADVANCED:
 * The user has strong foundational knowledge and wants to
 * study complex concepts or advanced applications.
 *
 * Product note:
 * This is a self-reported starting level. StudyFlow can later
 * refine recommendations using quiz results, course progress,
 * and assessment performance.
 */
public enum SkillLevel {

    COMPLETE_BEGINNER,
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}