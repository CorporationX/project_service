CREATE TABLE users (
    id bigint PRIMARY KEY,
    username varchar(64) UNIQUE NOT NULL,
    password varchar(128) NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    phone varchar(32) UNIQUE,
    about_me varchar(4096),
    active boolean DEFAULT true NOT NULL,
    city varchar(64),
    country_id bigint NOT NULL,
    experience int,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);
