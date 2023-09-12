CREATE TABLE if not exists offer (
  id BIGSERIAL PRIMARY KEY,
  vacancy_id BIGINT NOT NULL,
  candidate_id BIGINT NOT NULL,
  created_by BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  CONSTRAINT fk_offer_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancy (id),
  CONSTRAINT fk_offer_candidate FOREIGN KEY (candidate_id) REFERENCES candidate (id),
  CONSTRAINT fk_offer_team FOREIGN KEY (team_id) REFERENCES team (id)
);
