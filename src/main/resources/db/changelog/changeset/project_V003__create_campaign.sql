create table IF NOT EXISTS donation (
    id BIGSERIAL PRIMARY KEY,
    payment_number BIGINT,
    amount DECIMAL(19, 2),
    donation_time TIMESTAMP,
    currency VARCHAR(10),
    campaign_id BIGINT,
    user_id BIGINT
);

create TABLE IF NOT EXISTS campaign (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4096),
    goal DECIMAL(19,2),
    amount_raised DECIMAL(19,2),
    status VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    project_id BIGINT,
    currency VARCHAR(10),
    CONSTRAINT fk_project
        FOREIGN KEY (project_id) REFERENCES project(id),
    CONSTRAINT uc_title_project UNIQUE (title, project_id)
);