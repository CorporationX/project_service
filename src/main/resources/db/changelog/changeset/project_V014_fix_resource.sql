ALTER TABLE project_resource
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD CONSTRAINT fk_team_member_creator
        FOREIGN KEY (created_by) REFERENCES team_member (id),
    ADD CONSTRAINT fk_team_member_updater
        FOREIGN KEY (updated_by) REFERENCES team_member (id);

CREATE TABLE IF NOT EXISTS resource_allowed_roles
(
    resource_id BIGINT,
    role        varchar(20) NOT NULL,
    CONSTRAINT fk_resource_allowed_roles_resource_id
        FOREIGN KEY (resource_id) REFERENCES project_resource (id)
            ON DELETE CASCADE
);