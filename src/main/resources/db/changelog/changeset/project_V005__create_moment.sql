CREATE TABLE moment (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255),
  description TEXT,
  date TIMESTAMP NOT NULL,
  image_id VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP,
  created_by BIGINT,
  updated_by BIGINT
);

CREATE TABLE moment_resource (
  moment_id BIGINT,
  resource_id BIGINT,
  CONSTRAINT moment_resource_pk PRIMARY KEY (moment_id, resource_id),
  CONSTRAINT moment_resource_moment_fk FOREIGN KEY (moment_id) REFERENCES moment (id) ON DELETE CASCADE,
  CONSTRAINT moment_resource_resource_fk FOREIGN KEY (resource_id) REFERENCES resource (id) ON DELETE CASCADE
);

CREATE TABLE moment_project (
  moment_id BIGINT,
  project_id BIGINT,
  CONSTRAINT moment_project_pk PRIMARY KEY (moment_id, project_id),
  CONSTRAINT moment_project_moment_fk FOREIGN KEY (moment_id) REFERENCES moment (id) ON DELETE CASCADE,
  CONSTRAINT moment_project_project_fk FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);