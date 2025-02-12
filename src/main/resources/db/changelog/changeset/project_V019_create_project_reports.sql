CREATE TABLE IF NOT EXISTS project_reports (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT,
    file_key VARCHAR(255) NOT NULL,
    file_url VARCHAR(1024)
);