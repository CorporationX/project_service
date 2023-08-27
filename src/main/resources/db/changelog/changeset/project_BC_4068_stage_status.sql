alter table project_stage
    add column if not exists status varchar(255) not null default 'CREATED';
