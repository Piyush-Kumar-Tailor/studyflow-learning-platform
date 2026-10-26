-- =========================================================
-- V6: Course Enrollments and Lecture Progress
-- =========================================================

CREATE TABLE course_enrollments (
                                    id BIGSERIAL PRIMARY KEY,

                                    user_id BIGINT NOT NULL,
                                    course_id BIGINT NOT NULL,

                                    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',

                                    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    completed_at TIMESTAMP NULL,

                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_enrollment_user
                                        FOREIGN KEY (user_id)
                                            REFERENCES app_users(id)
                                            ON DELETE RESTRICT,

                                    CONSTRAINT fk_enrollment_course
                                        FOREIGN KEY (course_id)
                                            REFERENCES courses(id)
                                            ON DELETE RESTRICT,

                                    CONSTRAINT uq_enrollment_user_course
                                        UNIQUE (user_id, course_id),

                                    CONSTRAINT chk_enrollment_status
                                        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'DROPPED'))
);

CREATE INDEX idx_enrollments_user_id
    ON course_enrollments(user_id);

CREATE INDEX idx_enrollments_course_id
    ON course_enrollments(course_id);

CREATE INDEX idx_enrollments_status
    ON course_enrollments(status);


-- =========================================================
-- Lecture Progress
-- =========================================================

CREATE TABLE lecture_progress (
                                  id BIGSERIAL PRIMARY KEY,

                                  enrollment_id BIGINT NOT NULL,
                                  lecture_id BIGINT NOT NULL,

                                  status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',

                                  last_position_seconds INTEGER NOT NULL DEFAULT 0,
                                  completed_at TIMESTAMP NULL,

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_lecture_progress_enrollment
                                      FOREIGN KEY (enrollment_id)
                                          REFERENCES course_enrollments(id)
                                          ON DELETE RESTRICT,

                                  CONSTRAINT fk_lecture_progress_lecture
                                      FOREIGN KEY (lecture_id)
                                          REFERENCES lectures(id)
                                          ON DELETE RESTRICT,

                                  CONSTRAINT uq_enrollment_lecture_progress
                                      UNIQUE (enrollment_id, lecture_id),

                                  CONSTRAINT chk_lecture_progress_status
                                      CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED')),

                                  CONSTRAINT chk_lecture_progress_position
                                      CHECK (last_position_seconds >= 0)
);

CREATE INDEX idx_lecture_progress_enrollment
    ON lecture_progress(enrollment_id);

CREATE INDEX idx_lecture_progress_lecture
    ON lecture_progress(lecture_id);

CREATE INDEX idx_lecture_progress_status
    ON lecture_progress(status);