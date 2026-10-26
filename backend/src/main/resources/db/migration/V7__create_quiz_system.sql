-- =========================================================
-- V7: CREATE QUIZ AND ASSESSMENT SYSTEM
-- =========================================================

-- =========================================================
-- 1. QUIZZES
-- =========================================================

CREATE TABLE quizzes (
                         id BIGSERIAL PRIMARY KEY,

                         course_id BIGINT NOT NULL,
                         lecture_id BIGINT,

                         title VARCHAR(200) NOT NULL,
                         description TEXT,

                         duration_minutes INTEGER NOT NULL DEFAULT 30,
                         passing_percentage NUMERIC(5, 2) NOT NULL DEFAULT 40.00,

                         published BOOLEAN NOT NULL DEFAULT FALSE,

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_quizzes_course
                             FOREIGN KEY (course_id)
                                 REFERENCES courses(id)
                                 ON DELETE RESTRICT,

                         CONSTRAINT fk_quizzes_lecture
                             FOREIGN KEY (lecture_id)
                                 REFERENCES lectures(id)
                                 ON DELETE RESTRICT,

                         CONSTRAINT chk_quizzes_duration
                             CHECK (duration_minutes > 0),

                         CONSTRAINT chk_quizzes_passing_percentage
                             CHECK (
                                 passing_percentage >= 0
                                     AND passing_percentage <= 100
                                 )
);

CREATE INDEX idx_quizzes_course
    ON quizzes(course_id);

CREATE INDEX idx_quizzes_lecture
    ON quizzes(lecture_id);

CREATE INDEX idx_quizzes_published
    ON quizzes(published);


-- =========================================================
-- 2. QUIZ QUESTIONS
-- =========================================================

CREATE TABLE quiz_questions (
                                id BIGSERIAL PRIMARY KEY,

                                quiz_id BIGINT NOT NULL,

                                question_text TEXT NOT NULL,
                                sequence_number INTEGER NOT NULL,
                                marks INTEGER NOT NULL DEFAULT 1,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_quiz_questions_quiz
                                    FOREIGN KEY (quiz_id)
                                        REFERENCES quizzes(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT uq_quiz_question_sequence
                                    UNIQUE (quiz_id, sequence_number),

                                CONSTRAINT chk_quiz_question_sequence
                                    CHECK (sequence_number > 0),

                                CONSTRAINT chk_quiz_question_marks
                                    CHECK (marks > 0)
);

CREATE INDEX idx_quiz_questions_quiz
    ON quiz_questions(quiz_id);


-- =========================================================
-- 3. QUIZ OPTIONS
-- =========================================================

CREATE TABLE quiz_options (
                              id BIGSERIAL PRIMARY KEY,

                              question_id BIGINT NOT NULL,

                              option_text TEXT NOT NULL,
                              correct BOOLEAN NOT NULL DEFAULT FALSE,

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_quiz_options_question
                                  FOREIGN KEY (question_id)
                                      REFERENCES quiz_questions(id)
                                      ON DELETE CASCADE
);

CREATE INDEX idx_quiz_options_question
    ON quiz_options(question_id);

-- At most one correct option per question.
CREATE UNIQUE INDEX uq_quiz_one_correct_option
    ON quiz_options(question_id)
    WHERE correct = TRUE;


-- =========================================================
-- 4. QUIZ ATTEMPTS
-- =========================================================

CREATE TABLE quiz_attempts (
                               id BIGSERIAL PRIMARY KEY,

                               quiz_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,

                               started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               submitted_at TIMESTAMP,

                               score NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
                               total_marks NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
                               percentage NUMERIC(5, 2) NOT NULL DEFAULT 0.00,

                               passed BOOLEAN NOT NULL DEFAULT FALSE,

                               status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',

                               CONSTRAINT fk_quiz_attempts_quiz
                                   FOREIGN KEY (quiz_id)
                                       REFERENCES quizzes(id)
                                       ON DELETE RESTRICT,

                               CONSTRAINT fk_quiz_attempts_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES app_users(id)
                                       ON DELETE RESTRICT,

                               CONSTRAINT chk_quiz_attempt_status
                                   CHECK (
                                       status IN (
                                                  'IN_PROGRESS',
                                                  'SUBMITTED',
                                                  'EXPIRED'
                                           )
                                       ),

                               CONSTRAINT chk_quiz_attempt_score
                                   CHECK (score >= 0),

                               CONSTRAINT chk_quiz_attempt_total_marks
                                   CHECK (total_marks >= 0),

                               CONSTRAINT chk_quiz_attempt_percentage
                                   CHECK (
                                       percentage >= 0
                                           AND percentage <= 100
                                       )
);

CREATE INDEX idx_quiz_attempts_quiz_user
    ON quiz_attempts(quiz_id, user_id);

CREATE INDEX idx_quiz_attempts_user
    ON quiz_attempts(user_id);

CREATE INDEX idx_quiz_attempts_status
    ON quiz_attempts(status);


-- =========================================================
-- 5. QUIZ ATTEMPT ANSWERS
-- =========================================================

CREATE TABLE quiz_attempt_answers (
                                      id BIGSERIAL PRIMARY KEY,

                                      attempt_id BIGINT NOT NULL,
                                      question_id BIGINT NOT NULL,
                                      selected_option_id BIGINT,

                                      is_correct BOOLEAN NOT NULL DEFAULT FALSE,
                                      marks_awarded NUMERIC(10, 2) NOT NULL DEFAULT 0.00,

                                      answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT fk_quiz_answers_attempt
                                          FOREIGN KEY (attempt_id)
                                              REFERENCES quiz_attempts(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_quiz_answers_question
                                          FOREIGN KEY (question_id)
                                              REFERENCES quiz_questions(id)
                                              ON DELETE RESTRICT,

                                      CONSTRAINT fk_quiz_answers_option
                                          FOREIGN KEY (selected_option_id)
                                              REFERENCES quiz_options(id)
                                              ON DELETE RESTRICT,

                                      CONSTRAINT uq_quiz_attempt_question
                                          UNIQUE (attempt_id, question_id),

                                      CONSTRAINT chk_quiz_answer_marks
                                          CHECK (marks_awarded >= 0)
);

CREATE INDEX idx_quiz_answers_attempt
    ON quiz_attempt_answers(attempt_id);

CREATE INDEX idx_quiz_answers_question
    ON quiz_attempt_answers(question_id);