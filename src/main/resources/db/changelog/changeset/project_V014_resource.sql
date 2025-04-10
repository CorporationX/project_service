ALTER TABLE moment_resource DROP CONSTRAINT moment_resource_resource_fk;
ALTER TABLE resource RENAME TO project_resource;
ALTER TABLE moment_resource
ADD CONSTRAINT moment_resource_resource_fk
FOREIGN KEY (resource_id) REFERENCES project_resource (id) ON DELETE CASCADE;
alter table project_resource
add column if not exists created_by BIGINT,
add column if not exists updated_by BIGINT;
add column if not exists storage_key VARCHAR(255),
add column if not exists size BIGINT NOT NULL DEFAULT 0;