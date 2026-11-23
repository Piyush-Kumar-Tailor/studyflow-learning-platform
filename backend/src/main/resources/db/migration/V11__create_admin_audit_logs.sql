
CREATE TABLE admin_audit_logs (
                                  id BIGSERIAL PRIMARY KEY,

                                  admin_id BIGINT,
                                  target_user_id BIGINT,

                                  action VARCHAR(50) NOT NULL,

                                  old_value VARCHAR(100),
                                  new_value VARCHAR(100),

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_audit_admin
                                      FOREIGN KEY (admin_id)
                                          REFERENCES app_users(id)
                                          ON DELETE SET NULL,

                                  CONSTRAINT fk_audit_target_user
                                      FOREIGN KEY (target_user_id)
                                          REFERENCES app_users(id)
                                          ON DELETE SET NULL
);

CREATE INDEX idx_admin_audit_logs_admin_id
    ON admin_audit_logs(admin_id);

CREATE INDEX idx_admin_audit_logs_target_user_id
    ON admin_audit_logs(target_user_id);

CREATE INDEX idx_admin_audit_logs_created_at
    ON admin_audit_logs(created_at DESC);
