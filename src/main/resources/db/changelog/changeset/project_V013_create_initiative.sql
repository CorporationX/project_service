CREATE TABLE IF NOT EXISTS initiative (
    id              BIGSERIAL PRIMARY KEY,
    curator_id      BIGINT NOT NULL,
    project_id      BIGINT NOT NULL,
    name            VARCHAR(64) NOT NULL,
    description     VARCHAR(4096) NOT NULL,
    status          VARCHAR(32) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT fk_initiative_curator FOREIGN KEY (curator_id) REFERENCES team_member (id),
    CONSTRAINT fk_initiative_project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS initiative_project_stages (
    initiative_id       BIGINT NOT NULL,
    project_stage_id    BIGINT NOT NULL,
    CONSTRAINT fk_initiative_project_stages_initiative FOREIGN KEY (initiative_id) REFERENCES initiative (id),
    CONSTRAINT fk_initiative_project_stages_project_stage FOREIGN KEY (project_stage_id) REFERENCES project_stage (project_stage_id)
);

CREATE TABLE IF NOT EXISTS initiative_project (
    initiative_id   BIGINT NOT NULL,
    project_id      BIGINT NOT NULL,
    CONSTRAINT fk_initiative_project_stages_initiative FOREIGN KEY (initiative_id) REFERENCES initiative (id),
    CONSTRAINT fk_initiative_project_stages_project FOREIGN KEY (project_id) REFERENCES project (id)
);