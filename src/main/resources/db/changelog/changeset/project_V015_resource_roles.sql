CREATE TABLE resource_allowed_roles (
    id BIGSERIAL PRIMARY KEY,
    resource_id  BIGINT NOT NULL,
    role_id VARCHAR(16) NOT NULL,
    CONSTRAINT fk_resource_roles FOREIGN KEY (resource_id) REFERENCES project_resource (id)
);