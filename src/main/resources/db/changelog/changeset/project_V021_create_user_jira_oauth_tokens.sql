REATE TABLE user_jira_oauth_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    access_token VARCHAR(1000) NOT NULL,
    refresh_token VARCHAR(1000) NOT NULL,
    token_type VARCHAR(50) DEFAULT 'Bearer',
    expires_at TIMESTAMP NOT NULL,
    scope VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_oauth_tokens_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_oauth_tokens_user_id UNIQUE (user_id)
);

CREATE INDEX idx_user_jira_tokens_user_id ON user_jira_oauth_tokens(user_id);

CREATE INDEX idx_user_jira_tokens_expires_at ON user_jira_oauth_tokens(expires_at);

COMMENT ON TABLE user_jira_oauth_tokens IS 'OAuth tokens for Jira integration per user';
COMMENT ON COLUMN user_jira_oauth_tokens.expires_at IS 'Access token expiration timestamp';
