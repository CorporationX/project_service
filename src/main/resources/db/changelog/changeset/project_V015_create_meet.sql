CREATE TABLE IF NOT EXISTS meet
(
    id            BIGSERIAL,
    title         VARCHAR(256)          NOT NULL,
    description   VARCHAR(4096)         NULL,
    status        VARCHAR(100)          NULL,
    created_by    BIGINT                NOT NULL,
    team_id       BIGINT                NULL,
    project_id    BIGINT                NULL,
    start_date    TIMESTAMP              NULL,
    end_date      TIMESTAMP              NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_meet PRIMARY KEY (id)
);

ALTER TABLE meet
    ADD CONSTRAINT FK_MEET_ON_TEAM FOREIGN KEY (team_id) REFERENCES team (id);

ALTER TABLE meet
    ADD CONSTRAINT FK_MEET_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project (id);
