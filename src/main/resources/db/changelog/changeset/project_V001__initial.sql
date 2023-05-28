CREATE TABLE IF NOT EXISTS team_member (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS team (
    id BIGSERIAL PRIMARY KEY,
    team_member_id BIGINT,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_team_member
        FOREIGN KEY (team_member_id) REFERENCES team_member (id)
);

CREATE TABLE IF NOT EXISTS project (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(4096),
    storage_size BIGINT,
    max_storage_size BIGINT,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(255) NOT NULL,
    cover_image_id VARCHAR(255),
    team_id BIGINT,
    CONSTRAINT fk_owner
        FOREIGN KEY (owner_id) REFERENCES team_member(id),
    CONSTRAINT fk_team
        FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE TABLE IF NOT EXISTS resource (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    link VARCHAR(255),
    type VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT,
    size BIGINT,
    CONSTRAINT fk_project
        FOREIGN KEY (project_id) REFERENCES project(id)
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