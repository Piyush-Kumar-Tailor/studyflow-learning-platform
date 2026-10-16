package com.studyflow.backend.user.enums;

/**
 * Represents a user's education level.
 *
 * CLASS_8_OR_BELOW:
 * Students studying up to Class 8 or users with an equivalent
 * educational background.
 *
 * CLASS_10:
 * Users studying at or having completed Class 10.
 *
 * CLASS_12:
 * Users studying at or having completed Class 12.
 *
 * DIPLOMA:
 * Users pursuing or holding a diploma qualification.
 *
 * UNDERGRADUATE:
 * Users pursuing or holding a bachelor's degree, including
 * B.Tech, B.E., BCA, B.Sc., B.Com., and BA.
 *
 * POSTGRADUATE:
 * Users pursuing or holding a master's degree or equivalent.
 *
 * DOCTORAL:
 * Users pursuing or holding a doctoral qualification.
 *
 * WORKING_PROFESSIONAL:
 * Users whose primary learning context is professional
 * development or career advancement.
 *
 * OTHER:
 * Users whose education level does not fit another option.
 *
 * Product note:
 * This field represents the user's selected education category.
 * It should not be treated as verified academic qualification.
 */
public enum EducationLevel {

    CLASS_8_OR_BELOW,
    CLASS_10,
    CLASS_12,
    DIPLOMA,
    UNDERGRADUATE,
    POSTGRADUATE,
    DOCTORAL,
    WORKING_PROFESSIONAL,
    OTHER
}