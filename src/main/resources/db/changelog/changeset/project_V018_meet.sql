ALTER TABLE meet
    RENAME COLUMN google_event_id TO calendar_event_id;

ALTER TABLE meet
    ALTER COLUMN calendar_event_id TYPE VARCHAR(64);

ALTER TABLE meet
    ALTER COLUMN calendar_event_id SET NOT NULL;
