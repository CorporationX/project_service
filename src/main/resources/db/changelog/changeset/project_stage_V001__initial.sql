create table if not exists project_stage
(
    project_stage_id   bigserial primary key,
    project_stage_name varchar(255) not null,
    project_id         bigint       not null,

    constraint fk_project
        foreign key (project_id) references project (id)

);

create table if not exists project_stage_roles
(
    id               bigserial primary key,
    role             varchar(20) not null,
    count            int         not null,
    project_stage_id bigint      not null,

    constraint fk_project_stage
        foreign key (project_stage_id) references project_stage (project_stage_id)
);