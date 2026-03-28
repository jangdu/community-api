ALTER TABLE users
    ADD COLUMN bio VARCHAR(200) NULL AFTER avatar_url,
    ADD COLUMN status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE' AFTER bio,
    ADD COLUMN last_login_at DATETIME NULL AFTER status,
    ADD COLUMN deleted_at DATETIME NULL AFTER last_login_at;

CREATE INDEX idx_users_status ON users (status);
