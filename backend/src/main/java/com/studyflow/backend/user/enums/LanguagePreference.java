package com.studyflow.backend.user.enums;

/**
 * Represents a broad language preference for the learning experience.
 *
 * ENGLISH:
 * The user prefers English learning content.
 *
 * HINDI:
 * The user prefers Hindi learning content.
 *
 * HINGLISH:
 * The user prefers a mixture of Hindi and English.
 *
 * REGIONAL:
 * The user prefers another regional language.
 *
 * Architecture note:
 * These values are broad preference categories, not a complete
 * language catalog.
 *
 * When StudyFlow supports multiple Indian languages, consider
 * storing language codes such as en, hi, ta, te, bn, and mr
 * in a Language database table.
 *
 * This makes it easier to add languages without modifying
 * a Java enum and redeploying the backend.
 */
public enum LanguagePreference {

    ENGLISH,
    HINDI,
    HINGLISH,
    REGIONAL
}