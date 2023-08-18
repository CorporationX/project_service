CREATE TABLE IF NOT EXISTS jira
(
    id               BIGSERIAL PRIMARY KEY,
    username         VARCHAR(1024) NOT NULL,
    project_key      VARCHAR(16) NOT NULL,
    project_url      VARCHAR(1024) NOT NULL,
    project_id       BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_jira_project FOREIGN KEY (project_id) REFERENCES project (id)
);