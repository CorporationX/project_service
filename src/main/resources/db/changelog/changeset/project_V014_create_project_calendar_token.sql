CREATE TABLE calendar_token
(
    id            BIGSERIAL PRIMARY KEY,
    project_id       BIGINT       NOT NULL,
    access_token  VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_token_project FOREIGN KEY (project_id) REFERENCES project (id)
);