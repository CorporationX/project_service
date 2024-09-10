CREATE TABLE IF NOT EXISTS resource_allowed_roles
(
    resource_id BIGINT,
    role_id        varchar(20) NOT NULL,
    CONSTRAINT fk_resource_allowed_roles_resource_id
    FOREIGN KEY (resource_id) REFERENCES project_resource (id)
    ON DELETE CASCADE
    );