CREATE TABLE IF NOT EXISTS google_auth_tokens (
                                                  id SERIAL PRIMARY KEY,
                                                  access_token TEXT NOT NULL,
                                                  refresh_token TEXT NOT NULL,
                                                  expires_in INTEGER NOT NULL,
                                                  token_type VARCHAR(50) NOT NULL,
                                                  scope TEXT,
                                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);