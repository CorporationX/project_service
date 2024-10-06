ALTER TABLE meet
ADD COLUMN start_date timestamptz NOT NULL,
ADD COLUMN end_date timestamptz NOT NULL,
ADD COLUMN google_event_id BIGINT NOT NULL;
