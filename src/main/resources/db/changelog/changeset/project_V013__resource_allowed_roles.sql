create table resource_allowed_roles(
    role_id BIGINT,
    resource_id BIGINT,
    constraint FK_allowed_roles foreign key (resource_id) references resource (id));