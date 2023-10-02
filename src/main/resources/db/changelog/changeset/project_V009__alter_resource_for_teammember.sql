ALTER TABLE resource
    ADD created_by BIGINT NOT NULL,
    ADD updated_by BIGINT,
    ADD CONSTRAINT fk_resource_team_member_created FOREIGN KEY (created_by) REFERENCES team_member (id),
    ADD CONSTRAINT fk_resource_team_member_update FOREIGN KEY (updated_by) REFERENCES team_member (id);

create table if not exists resource_allowed_roles
(
    resource_id bigint,
    role varchar(20) NOT NULL,
    CONSTRAINT fk_resource_roles_team_member FOREIGN KEY (resource_id) REFERENCES resource (id)
);