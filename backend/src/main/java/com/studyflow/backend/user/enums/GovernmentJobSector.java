package com.studyflow.backend.user.enums;

/**
 * Represents government employment sectors supported by StudyFlow.
 *
 * STATE_ADMINISTRATION:
 * Administrative and departmental positions in state government.
 *
 * REVENUE_AND_LAND_RECORDS:
 * Revenue, land records, Patwari, and related departmental roles.
 *
 * STATE_CLERICAL_JOBS:
 * Clerical, office assistant, and administrative support roles
 * in state government departments.
 *
 * STATE_GROUP_C_AND_D:
 * Applicable state-level Group C and Group D recruitment areas.
 *
 * STATE_POLICE:
 * State police recruitment and related positions.
 *
 * STATE_TEACHING:
 * State-level school teaching and related education positions.
 *
 * STATE_ENGINEERING:
 * State government engineering and technical departments.
 *
 * STATE_HEALTHCARE:
 * State healthcare and medical department positions.
 *
 * CENTRAL_ADMINISTRATION:
 * Central government administrative and departmental positions.
 *
 * SSC_RECRUITMENT:
 * Government recruitment opportunities associated with SSC exams.
 *
 * RAILWAY_RECRUITMENT:
 * Railway recruitment opportunities, including technical and
 * non-technical positions.
 *
 * BANKING_RECRUITMENT:
 * Banking recruitment, including officer and clerical roles.
 *
 * DEFENCE_RECRUITMENT:
 * Defence services and related recruitment opportunities.
 *
 * PARAMILITARY_RECRUITMENT:
 * Paramilitary and central armed police force opportunities.
 *
 * JUDICIAL_SERVICES:
 * Judicial service and related court-system positions.
 *
 * PUBLIC_SECTOR_UNDERTAKING:
 * Recruitment for public-sector undertaking positions.
 *
 * OTHER:
 * Government employment sectors not represented above.
 *
 * Architecture note:
 * Use a separate database entity for individual departments,
 * recruiting organizations, states, and job positions when
 * detailed filtering becomes necessary.
 */
public enum GovernmentJobSector {

    STATE_ADMINISTRATION,
    REVENUE_AND_LAND_RECORDS,
    STATE_CLERICAL_JOBS,
    STATE_GROUP_C_AND_D,
    STATE_POLICE,
    STATE_TEACHING,
    STATE_ENGINEERING,
    STATE_HEALTHCARE,
    CENTRAL_ADMINISTRATION,
    SSC_RECRUITMENT,
    RAILWAY_RECRUITMENT,
    BANKING_RECRUITMENT,
    DEFENCE_RECRUITMENT,
    PARAMILITARY_RECRUITMENT,
    JUDICIAL_SERVICES,
    PUBLIC_SECTOR_UNDERTAKING,
    OTHER
}