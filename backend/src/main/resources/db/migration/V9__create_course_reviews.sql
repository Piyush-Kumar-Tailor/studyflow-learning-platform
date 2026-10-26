CREATE TABLE course_reviews (
                                id BIGSERIAL PRIMARY KEY,

                                course_id BIGINT NOT NULL,
                                user_id BIGINT NOT NULL,

                                rating SMALLINT NOT NULL,
                                review_text TEXT,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_course_reviews_course
                                    FOREIGN KEY (course_id)
                                        REFERENCES courses(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_course_reviews_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES app_users(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT chk_course_reviews_rating
                                    CHECK (rating BETWEEN 1 AND 5),

                                CONSTRAINT uq_course_reviews_course_user
                                    UNIQUE (course_id, user_id)
);

CREATE INDEX idx_course_reviews_course_created
    ON course_reviews(course_id, created_at DESC);

CREATE INDEX idx_course_reviews_user_updated
    ON course_reviews(user_id, updated_at DESC);