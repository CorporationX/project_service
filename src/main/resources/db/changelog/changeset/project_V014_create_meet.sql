CREATE TABLE IF NOT EXISTS meet
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(128) NOT NULL,
    description      VARCHAR(4096),
    location         VARCHAR(512),
    time_zone        VARCHAR(255),
    event_google_id  VARCHAR(128),
    event_google_url VARCHAR(255),
    created_by       BIGINT,
    project_id BIGINT NOT NULL,
    meet_status       VARCHAR(50)  NOT NULL,
    start_date_time   TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date_time     TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP
);