CREATE TABLE IF NOT EXISTS event_mapping (
                                         id UUID PRIMARY KEY,
                                         event_id BIGINT NOT NULL UNIQUE,
                                         google_event_id VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS google_auth_tokens (
                                              id SERIAL PRIMARY KEY,
                                              access_token TEXT NOT NULL,
                                              refresh_token TEXT NOT NULL,
                                              expires_in BIGINT NOT NULL,
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);