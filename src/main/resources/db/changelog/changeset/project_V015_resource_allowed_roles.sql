CREATE TABLE IF NOT EXISTS resource_allowed_roles (
    resource_id     BIGINT  NOT NULL,
    role_id         varchar NOT NULL,
    CONSTRAINT fk_initiative_resource FOREIGN KEY (resource_id) REFERENCES project_resource (id),
    PRIMARY KEY(resource_id, role_id)
)