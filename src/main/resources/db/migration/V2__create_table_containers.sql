CREATE TABLE containers (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    container_id  VARCHAR(255),
    name          VARCHAR(255),
    port          INTEGER,
    proxy_host_id INTEGER,
    status        VARCHAR(50)  NOT NULL,
    user_id       UUID         NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_container_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);