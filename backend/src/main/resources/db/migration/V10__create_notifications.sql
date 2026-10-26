CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,

                               user_id BIGINT NOT NULL,

                               title VARCHAR(200) NOT NULL,
                               message TEXT NOT NULL,

                               type VARCHAR(50) NOT NULL,

                               reference_id BIGINT,

                               is_read BOOLEAN NOT NULL DEFAULT FALSE,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               read_at TIMESTAMP,

                               CONSTRAINT fk_notifications_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES app_users(id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user_created
    ON notifications(user_id, created_at DESC);

CREATE INDEX idx_notifications_user_unread
    ON notifications(user_id, is_read, created_at DESC);