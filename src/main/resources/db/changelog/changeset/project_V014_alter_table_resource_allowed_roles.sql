alter table resource_allowed_roles
    rename column role_id to role;

alter table resource_allowed_roles
    alter column role type varchar using role::varchar;
