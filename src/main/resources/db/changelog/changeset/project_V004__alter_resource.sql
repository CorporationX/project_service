alter table project_resource
add column if not exists created_by BIGINT,
add column if not exists updated_by BIGINT;