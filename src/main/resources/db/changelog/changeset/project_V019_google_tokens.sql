CREATE TABLE google_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    access_token VARCHAR(255),
    refresh_token VARCHAR(255),
    expires_in TIMESTAMP,
    token_type VARCHAR(50),
    scope VARCHAR(255),
    UNIQUE (user_id)
);

CREATE INDEX idx_google_tokens_access_token ON google_tokens (access_token);

CREATE INDEX idx_google_tokens_expires_in ON google_tokens (expires_in);
