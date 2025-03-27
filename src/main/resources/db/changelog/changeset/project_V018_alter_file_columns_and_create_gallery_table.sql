ALTER TABLE project
ADD COLUMN presentation_file_key VARCHAR(255),
ADD COLUMN presentation_generated_at TIMESTAMP;

ALTER TABLE team
ADD COLUMN avatar_key VARCHAR(255);

ALTER TABLE vacancy
ADD COLUMN cover_image_key VARCHAR(255),
ADD COLUMN position VARCHAR(50) NOT NULL;

CREATE TABLE project_gallery (
    project_id BIGINT NOT NULL,
    file_key VARCHAR(255) NOT NULL,
    PRIMARY KEY (project_id, file_key),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);