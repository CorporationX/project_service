CREATE TABLE IF NOT EXISTS google_calendar_events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    url VARCHAR(1024),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    ended BOOLEAN NOT NULL DEFAULT FALSE,
    canceled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS event_attendee (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    optional BOOLEAN DEFAULT FALSE,
    response_status VARCHAR(50),
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_event FOREIGN KEY (event_id)
    REFERENCES google_calendar_events (id) ON DELETE CASCADE
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'google_calendar_events'
          AND column_name = 'location'
    ) THEN
ALTER TABLE google_calendar_events ADD COLUMN location VARCHAR(255);
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'google_calendar_events'
          AND column_name = 'google_calendar_id'
    ) THEN
ALTER TABLE google_calendar_events ADD COLUMN google_calendar_id VARCHAR(255);
END IF;
END $$;