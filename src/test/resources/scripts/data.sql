create TABLE IF NOT EXISTS campaign (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4096),
    goal DECIMAL(19,2),
    amount_raised DECIMAL(19,2),
    status VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    project_id BIGINT,
    currency VARCHAR(10),
    removed boolean
);

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
    cover_image_id   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    username varchar(64) UNIQUE NOT NULL,
    password varchar(128) NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    phone varchar(32) UNIQUE,
    about_me varchar(4096),
    active boolean DEFAULT true NOT NULL,
    city varchar(64),
    country_id bigint NOT NULL,
    experience int,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS team
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS team_member
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL
);

create table if not exists team_member_roles
(
    team_member_id bigint,
    role           varchar(20) NOT NULL
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
    size       BIGINT
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
                                    stage_id BIGINT
);

CREATE TABLE IF NOT EXISTS schedule (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        description VARCHAR(255),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        project_id BIGINT

);

INSERT INTO users (username, password, email, phone, about_me, active, city, country_id, experience)
VALUES ('john_doe', 'securePassword123', 'john.doe@example.com', '+1234567890', 'Software developer with 5 years of experience', true, 'New York', 1, 5);

INSERT INTO users (username, password, email, phone, about_me, active, city, country_id, experience)
VALUES ('jane_smith', 'anotherSecurePassword456', 'jane.smith@example.com', '+0987654321', 'Data scientist passionate about AI and ML', true, 'San Francisco', 2, 3);

INSERT INTO project (name, description, status, visibility)
VALUES ('AI Research Project', 'A project focused on artificial intelligence and machine learning', 'CREATED', 'PUBLIC');

insert into team (project_id) values (1);

insert into team_member (user_id, team_id) values (1, 1);
insert into team_member (user_id, team_id) values (2, 1);

insert into team_member_roles (team_member_id, role) values (1, 'MANAGER');
insert into team_member_roles (team_member_id, role) values (2, 'DEVELOPER');

insert into campaign  (title, description, status) values ('title', 'descr', 'ACTIVE');