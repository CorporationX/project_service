CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    title VARCHAR(128) NOT NULL,
    calendar_event_id VARCHAR(64) NOT NULL,
    description VARCHAR(512) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_event_user FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS event_participant (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_participant_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users (id)
);