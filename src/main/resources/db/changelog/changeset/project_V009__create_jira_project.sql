CREATE TABLE jira_project (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(16) NOT NULL,
    name VARCHAR(32),
    username VARCHAR(32) NOT NULL,
    password VARCHAR NOT NULL,
    url VARCHAR(128) NOT NULL,
    created_at     timestamptz DEFAULT current_timestamp,
    updated_at     timestamptz DEFAULT current_timestamp
);