-- =========================================================
-- V8: STUDENT BOOKMARKS AND PERSONAL NOTES
-- =========================================================

-- ---------------------------------------------------------
-- 1. LECTURE BOOKMARKS
-- ---------------------------------------------------------

CREATE TABLE lecture_bookmarks (
                                   id BIGSERIAL PRIMARY KEY,

                                   user_id BIGINT NOT NULL,
                                   lecture_id BIGINT NOT NULL,

                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_lecture_bookmarks_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES app_users(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_lecture_bookmarks_lecture
                                       FOREIGN KEY (lecture_id)
                                           REFERENCES lectures(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT uq_lecture_bookmarks_user_lecture
                                       UNIQUE (user_id, lecture_id)
);

CREATE INDEX idx_lecture_bookmarks_user_created
    ON lecture_bookmarks(user_id, created_at DESC);


-- ---------------------------------------------------------
-- 2. STUDENT PERSONAL NOTES
-- ---------------------------------------------------------

CREATE TABLE lecture_notes (
                               id BIGSERIAL PRIMARY KEY,

                               user_id BIGINT NOT NULL,
                               lecture_id BIGINT NOT NULL,

                               title VARCHAR(200) NOT NULL,
                               content TEXT NOT NULL,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_lecture_notes_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES app_users(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_lecture_notes_lecture
                                   FOREIGN KEY (lecture_id)
                                       REFERENCES lectures(id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_lecture_notes_user_updated
    ON lecture_notes(user_id, updated_at DESC);

CREATE INDEX idx_lecture_notes_lecture
    ON lecture_notes(lecture_id);