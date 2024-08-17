CREATE TABLE IF NOT EXISTS meet
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(4096),
    location    VARCHAR(255),
    team_id     BIGINT       NOT NULL,
    meet_status VARCHAR(64),
    start_date  TIMESTAMP    NOT NULL,
    end_date    TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL,
    created_by  BIGINT       NOT NULL,

    CONSTRAINT fk_meet_team FOREIGN KEY (team_id) REFERENCES team (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
