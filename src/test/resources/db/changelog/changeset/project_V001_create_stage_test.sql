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

create table if not exists project_stage_executors
(
    id          bigserial primary key,
    stage_id    bigint not null,
    executor_id bigint not null,


    constraint fk_project_stage
        foreign key (stage_id) references project_stage (project_stage_id),
    constraint fk_team_member
        foreign key (executor_id) references team_member (id)
);

create index project_stage_executors_stage_id_idx on project_stage_executors (stage_id);