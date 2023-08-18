create table if not exists role_to_stage
(
    id              bigserial primary key,
    stage_id        bigint not null,
    stage_roles_id  bigint not null,

    constraint fk_project_stage_role
    foreign key (stage_roles_id) references project_stage_roles (id),
    constraint fk_project_stage
    foreign key (stage_id) references project_stage (project_stage_id)
);