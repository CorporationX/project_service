CREATE TABLE IF NOT EXISTS project
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(128) NOT NULL,
    description      VARCHAR(4096),
    parent_project_id        BIGINT,
    storage_size     BIGINT,
    max_storage_size BIGINT,
    owner_id         BIGINT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status           VARCHAR(255) NOT NULL,
    visibility       VARCHAR(255) NOT NULL,
    cover_image_id   VARCHAR(255),
    CONSTRAINT fk_project_parent FOREIGN KEY (parent_project_id) REFERENCES project (id)
);