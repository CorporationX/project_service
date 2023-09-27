CREATE TABLE moment_team_member (
    moment_id BIGINT,
    team_member_id BIGINT,
    CONSTRAINT moment_team_member_pk PRIMARY KEY (moment_id, team_member_id),
    CONSTRAINT moment_team_member_moment_fk FOREIGN KEY (moment_id) REFERENCES moment (id) ON DELETE CASCADE,
    CONSTRAINT moment_team_member_team_member_fk FOREIGN KEY (team_member_id) REFERENCES team_member (id) ON DELETE CASCADE
);