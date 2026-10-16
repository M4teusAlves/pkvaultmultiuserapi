CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    login       VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        INTEGER NOT NULL,
    actived     BOOLEAN NOT NULL DEFAULT TRUE,
    activation_token VARCHAR(255),
    activation_token_expiry TIMESTAMP,
    last_heartbeat TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);