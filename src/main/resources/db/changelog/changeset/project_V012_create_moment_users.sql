CREATE TABLE moment_user (

  moment_id BIGINT,
  team_member_id BIGINT,
  CONSTRAINT moment_user_pk PRIMARY KEY (moment_id, team_member_id),
  CONSTRAINT user_moment_fk FOREIGN KEY (moment_id) REFERENCES moment (id) ON DELETE CASCADE,
  CONSTRAINT team_member_fk FOREIGN KEY (team_member_id) REFERENCES team_member (id) ON DELETE CASCADE
);