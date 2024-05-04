ALTER TABLE resource
    ADD COLUMN created_by INT,
    ADD COLUMN updated_by INT;

ALTER TABLE resource
    ADD CONSTRAINT fk_resource_created_by
        FOREIGN KEY (created_by)
            REFERENCES team_member(id),
    ADD CONSTRAINT fk_resource_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES team_member(id);
CREATE TABLE resource_allowed_roles(
    role_id bigint,
    resource_id bigint,

    CONSTRAINT fk_allowed_roles FOREIGN KEY (resource_id) REFERENCES resource(id)
);