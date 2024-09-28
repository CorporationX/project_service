CREATE TABLE IF NOT EXISTS event_mapping (
                               id BIGINT PRIMARY KEY,
                               event_id BIGINT NOT NULL UNIQUE,
                               google_event_id VARCHAR(255) NOT NULL
);