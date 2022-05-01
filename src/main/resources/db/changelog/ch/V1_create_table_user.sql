--liquibase formatted sql
--changeset v:1
CREATE TABLE users
(
    id              serial              PRIMARY KEY,
    name            varchar(100)        NOT NULL,
    password_hash   varchar(255)        NOT NULL,
    token           varchar(255),
    email           varchar(100)        NOT NULL,
    img             text                NOT NULL DEFAULT 'default.png',
    created         timestamp           NOT NULL DEFAULT NOW(),
    updated         timestamp           NOT NULL DEFAULT NOW()
)