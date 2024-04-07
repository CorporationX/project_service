CREATE TABLE resource_allowed_roles (
    role_id BIGINT,
    resource_id BIGINT,
    CONSTRAINT FK_allowed_roles FOREIGN KEY (resource_id) REFERENCES resource (id)
);