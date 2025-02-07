CREATE TABLE IF NOT EXISTS meet (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    title VARCHAR(128) NOT NULL,
    description VARCHAR(512) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_meet_project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS meet_participant (
    id BIGSERIAL PRIMARY KEY,
    meet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_participant_meet FOREIGN KEY (meet_id) REFERENCES meet (id)
);