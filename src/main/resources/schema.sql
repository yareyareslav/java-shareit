DROP CASCADE TABLE IF EXISTS users
DROP CASCADE TABLE IF EXISTS items;

CREATE TABLE IF NOT EXISTS users (
    id id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,

    CONSTRAINT fk_items_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id),

    CONSTRAINT fk_items_request
        FOREIGN KEY (request_id)
        REFERENCES requests(id)
);