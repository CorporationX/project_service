ALTER TABLE event_mapping
DROP CONSTRAINT IF EXISTS event_mapping_event_id_key;

ALTER TABLE event_mapping
DROP CONSTRAINT IF EXISTS event_mapping_pkey;

ALTER TABLE event_mapping
ALTER COLUMN id TYPE BIGINT;

ALTER TABLE event_mapping
    ADD PRIMARY KEY (id);