ALTER TABLE event_mapping
DROP CONSTRAINT IF EXISTS event_mapping_event_id_key;

ALTER TABLE event_mapping
DROP CONSTRAINT IF EXISTS event_mapping_pkey;

ALTER TABLE event_mapping
    ALTER COLUMN id SET DATA TYPE BIGINT;

CREATE SEQUENCE IF NOT EXISTS event_mapping_seq;

ALTER TABLE event_mapping
    ALTER COLUMN id SET DEFAULT nextval('event_mapping_seq');

ALTER TABLE event_mapping
    ADD PRIMARY KEY (id);