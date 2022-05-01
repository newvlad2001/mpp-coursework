--liquibase formatted sql
--changeset maxim:7
CREATE TABLE tokens(
    id          serial          PRIMARY KEY,
    token       varchar(255)    NOT NULL,
    user_id     int             NOT NULL,

    CONSTRAINT fk_tokens_users FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)