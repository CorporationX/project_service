create table if not exists google_tokens (
    id serial primary key,
    uuid varchar(255),
    oauth_client_id varchar(255),
    access_token varchar(255),
    refresh_token varchar(255),
    expiration bigint,
    updated_at timestamp,
    user_id bigint
)