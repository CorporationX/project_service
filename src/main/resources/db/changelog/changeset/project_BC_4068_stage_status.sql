alter table project_stage
    add column if not exists project_stage_status varchar(255) not null default 'CREATED';
