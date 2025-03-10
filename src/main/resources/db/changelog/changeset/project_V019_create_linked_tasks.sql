CREATE TABLE IF NOT EXISTS task_linked_tasks (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    linked_task_id BIGINT NOT NULL,
    CONSTRAINT fk_task_id FOREIGN KEY (task_id) REFERENCES task (id),
    CONSTRAINT fk_linked_task_id FOREIGN KEY (linked_task_id) REFERENCES task (id),
    CONSTRAINT uc_task_linked_task UNIQUE (task_id, linked_task_id) -- Уникальность пары
);