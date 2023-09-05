ALTER TABLE resource
    ADD COLUMN created_by BIGINT,
    ADD CONSTRAINT fk_created_by
        FOREIGN KEY (created_by)
        REFERENCES team_member (id);

ALTER TABLE resource
    ADD COLUMN updated_by BIGINT,
    ADD CONSTRAINT fk_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES team_member (id);

ALTER TABLE project
    ADD COLUMN version BIGINT;

