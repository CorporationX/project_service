CREATE TABLE internship (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT NOT NULL,
  mentor_id BIGINT NOT NULL,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP,
  status VARCHAR(50) NOT NULL,
  description TEXT,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP,
  created_by BIGINT NOT NULL,
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
