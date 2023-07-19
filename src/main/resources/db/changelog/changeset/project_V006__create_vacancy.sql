CREATE TABLE candidate (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  resume_doc_key VARCHAR(255),
  cover_letter TEXT
);

CREATE TABLE vacancy (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP,
  created_by BIGINT,
  updated_by BIGINT,
  status VARCHAR(50) NOT NULL,
  salary DECIMAL,
  work_schedule VARCHAR(255)
);

CREATE TABLE vacancy_skills (
  vacancy_id BIGINT,
  skill_id BIGINT,
  CONSTRAINT fk_vacancy_skills_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancy (id)
);