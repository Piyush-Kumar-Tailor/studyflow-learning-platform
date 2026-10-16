package com.studyflow.backend.user.enums;

/**
 * Represents a user's preferred learning delivery mode.
 *
 * SELF_PACED_RECORDED:
 * The user prefers recorded lessons that can be studied
 * independently at a flexible pace.
 *
 * LIVE_ONLINE:
 * The user prefers scheduled online classes and live teaching.
 *
 * HYBRID:
 * The user prefers a combination of recorded, live, or
 * other supported learning formats.
 *
 * OFFLINE:
 * The user prefers in-person classroom or offline instruction.
 *
 * Product note:
 * StudyFlow's initial YouTube-based learning experience is
 * primarily self-paced. Other modes should only be offered
 * as available features, not as a promise of existing services.
 */
public enum PreferredMode {

    SELF_PACED_RECORDED,
    LIVE_ONLINE,
    HYBRID,
    OFFLINE
}