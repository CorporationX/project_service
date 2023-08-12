ALTER TABLE candidate
ADD COLUMN vacancy_id BIGINT,
ADD CONSTRAINT fk_vacancy
    FOREIGN KEY (vacancy_id)
    REFERENCES vacancy(id);
