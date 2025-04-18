CREATE TABLE if not exists project_gallery (
    id bigserial PRIMARY KEY,
    project_id bigint NOT NULL,
    file_key VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint fk_project
            foreign key (project_id) references project (id)
);