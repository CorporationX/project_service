CREATE TABLE IF NOT EXISTS project
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(128) NOT NULL,
    description      VARCHAR(4096),
    parent_project_id        BIGINT,
    storage_size     BIGINT,
    max_storage_size BIGINT,
    owner_id         BIGINT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status           VARCHAR(255) NOT NULL,
    visibility       VARCHAR(255) NOT NULL,
    cover_image_id   VARCHAR(255),
    CONSTRAINT fk_project_parent FOREIGN KEY (parent_project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS team
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_team_project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS team_member
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team (id)
);

create table if not exists team_member_roles
(
    team_member_id bigint,
    role           varchar(20) NOT NULL,
    CONSTRAINT fk_team_member_roles_team_member FOREIGN KEY (team_member_id) REFERENCES team_member (id)
);

CREATE TABLE IF NOT EXISTS resource
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    key        VARCHAR(255),
    type       VARCHAR(255),
    status     VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT,
    size       BIGINT,
    CONSTRAINT fk_project
        FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS task (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    description TEXT,
                                    status VARCHAR(255),
                                    performer_user_id BIGINT NOT NULL,
                                    reporter_user_id BIGINT NOT NULL,
                                    minutes_tracked INT,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    parent_task_id BIGINT,
                                    project_id BIGINT,
                                    stage_id BIGINT,
                                    CONSTRAINT fk_parent_task
                                        FOREIGN KEY (parent_task_id) REFERENCES task(id),
                                    CONSTRAINT fk_project
                                        FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE TABLE IF NOT EXISTS schedule (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        description VARCHAR(255),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        project_id BIGINT,
                                        CONSTRAINT fk_project
                                            FOREIGN KEY (project_id) REFERENCES project(id)
);
