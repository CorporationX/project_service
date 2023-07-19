CREATE TABLE internship (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT,
  mentor_id BIGINT,
  start_date TIMESTAMP,
  end_date TIMESTAMP,
  status VARCHAR(255),
  description TEXT,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  created_by BIGINT,
  updated_by BIGINT,
  schedule_id BIGINT,
  CONSTRAINT fk_internship_project FOREIGN KEY (project_id) REFERENCES project (id),
  CONSTRAINT fk_internship_mentor FOREIGN KEY (mentor_id) REFERENCES team_member (id),
  CONSTRAINT fk_internship_schedule FOREIGN KEY (schedule_id) REFERENCES schedule (id)
);

CREATE TABLE internship_interns (
  internship_id BIGINT,
  team_member_id BIGINT,
  CONSTRAINT fk_internship_interns_internship FOREIGN KEY (internship_id) REFERENCES internship (id),
  CONSTRAINT fk_internship_interns_team_member FOREIGN KEY (team_member_id) REFERENCES team_member (id)
);
