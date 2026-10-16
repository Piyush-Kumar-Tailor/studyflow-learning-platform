package com.studyflow.backend.user.enums;

/**
 * Defines broad categories of examinations supported by StudyFlow.
 *
 * SCHOOL_BOARD:
 * School examinations and board-level academic preparation.
 *
 * ENGINEERING_ENTRANCE:
 * Entrance examinations for engineering and related programs.
 *
 * MEDICAL_ENTRANCE:
 * Entrance examinations for medical and related health programs.
 *
 * LAW_ENTRANCE:
 * Entrance examinations for law programs.
 *
 * MANAGEMENT_ENTRANCE:
 * Entrance examinations for management and business programs.
 *
 * UNIVERSITY_ENTRANCE:
 * General undergraduate university entrance examinations.
 *
 * POSTGRADUATE_ENTRANCE:
 * Entrance examinations for postgraduate programs.
 *
 * CENTRAL_GOVERNMENT:
 * Recruitment examinations for central government positions.
 *
 * STATE_GOVERNMENT:
 * Recruitment examinations conducted for state government jobs.
 *
 * CIVIL_SERVICES:
 * Civil service and administrative service examinations.
 *
 * RAILWAY:
 * Recruitment examinations for railway positions.
 *
 * BANKING:
 * Recruitment examinations for banking positions.
 *
 * INSURANCE:
 * Recruitment examinations for insurance-sector positions.
 *
 * TEACHING_ELIGIBILITY:
 * Teacher eligibility and related teaching recruitment exams.
 *
 * POLICE_AND_PARAMILITARY:
 * Police, paramilitary, and related recruitment examinations.
 *
 * DEFENCE:
 * Defence recruitment and officer-entry examinations.
 *
 * JUDICIARY:
 * Judicial service and related legal recruitment examinations.
 *
 * ENGINEERING_GOVERNMENT_JOBS:
 * Recruitment examinations for government engineering roles.
 *
 * TECHNICAL_GOVERNMENT_JOBS:
 * Recruitment examinations for other technical government roles.
 *
 * PUBLIC_SECTOR_UNDERTAKING:
 * Recruitment examinations for public-sector undertakings.
 *
 * STUDY_ABROAD:
 * Examinations used for international education admissions.
 *
 * PROFESSIONAL_CERTIFICATION:
 * Professional, industry, and skill certification examinations.
 *
 * OTHER:
 * Examinations that do not fit another category.
 *
 * Architecture note:
 * Individual examination names, eligibility rules, syllabi,
 * conducting authorities, and active status belong in database
 * tables, not in this enum.
 */
public enum ExamCategory {

    SCHOOL_BOARD,
    ENGINEERING_ENTRANCE,
    MEDICAL_ENTRANCE,
    LAW_ENTRANCE,
    MANAGEMENT_ENTRANCE,
    UNIVERSITY_ENTRANCE,
    POSTGRADUATE_ENTRANCE,
    CENTRAL_GOVERNMENT,
    STATE_GOVERNMENT,
    CIVIL_SERVICES,
    RAILWAY,
    BANKING,
    INSURANCE,
    TEACHING_ELIGIBILITY,
    POLICE_AND_PARAMILITARY,
    DEFENCE,
    JUDICIARY,
    ENGINEERING_GOVERNMENT_JOBS,
    TECHNICAL_GOVERNMENT_JOBS,
    PUBLIC_SECTOR_UNDERTAKING,
    STUDY_ABROAD,
    PROFESSIONAL_CERTIFICATION,
    OTHER
}