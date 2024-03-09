ALTER TABLE resource
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD FOREIGN KEY (created_by) REFERENCES team_member(id),
    ADD FOREIGN KEY (updated_by) REFERENCES team_member(id)