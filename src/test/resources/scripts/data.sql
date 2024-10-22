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
    currency VARCHAR(10)
);