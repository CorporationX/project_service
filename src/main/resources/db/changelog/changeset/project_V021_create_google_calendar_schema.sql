CREATE TABLE google_calendar (
                                 id BIGSERIAL PRIMARY KEY,
                                 google_account_email VARCHAR(255) NOT NULL,
                                 calendar_id VARCHAR(255) NOT NULL, -- ID из Google API
                                 summary VARCHAR(255),
                                 time_zone VARCHAR(255),
                                 created_at TIMESTAMP DEFAULT NOW(),
                                 updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE google_calendar_events (
                                        id BIGSERIAL PRIMARY KEY,
                                        google_calendar_id BIGINT NOT NULL,
                                        title VARCHAR(255) NOT NULL,
                                        description TEXT,
                                        url VARCHAR(1024),
                                        start_time TIMESTAMP NOT NULL,
                                        end_time TIMESTAMP NOT NULL,
                                        ended BOOLEAN NOT NULL DEFAULT FALSE,
                                        canceled BOOLEAN NOT NULL DEFAULT FALSE,
                                        created_at TIMESTAMP DEFAULT NOW(),
                                        updated_at TIMESTAMP DEFAULT NOW(),
                                        CONSTRAINT fk_calendar FOREIGN KEY (google_calendar_id)
                                            REFERENCES google_calendar (id) ON DELETE CASCADE
);

CREATE TABLE event_attendee (
                                id BIGSERIAL PRIMARY KEY,
                                event_id BIGINT NOT NULL,
                                email VARCHAR(255) NOT NULL,
                                display_name VARCHAR(255),
                                optional BOOLEAN DEFAULT FALSE,
                                response_status VARCHAR(50),
                                role VARCHAR(50),
                                created_at TIMESTAMP DEFAULT NOW(),
                                updated_at TIMESTAMP DEFAULT NOW(),
                                CONSTRAINT fk_event FOREIGN KEY (event_id)
                                    REFERENCES google_calendar_events (id) ON DELETE CASCADE
);