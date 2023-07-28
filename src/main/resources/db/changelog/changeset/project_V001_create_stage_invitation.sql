create table if not exists stage_invitation
(
    id          bigserial primary key,
    stage_id    bigint      not null,
    author      bigint      not null,
    invited     bigint      not null,
    description varchar(255),
    status      varchar(20) not null,

    constraint fk_stage_id
        foreign key (stage_id) references project_stage (project_stage_id),
    constraint fk_author
        foreign key (author) references team_member (id),
    constraint fk_invited
        foreign key (invited) references team_member (id)
);