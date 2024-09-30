CREATE TABLE accounts.rol
(
    id     bigint AUTO_INCREMENT NOT NULL,
    name   varchar(50) NOT NULL,
    status boolean     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT name_uq UNIQUE (name)
);

INSERT INTO accounts.rol (name, status)
VALUES ('ADMIN', TRUE);
INSERT INTO accounts.rol (name, status)
VALUES ('CLIENT', TRUE);
INSERT INTO accounts.rol (name, status)
VALUES ('SUPPORT', TRUE);


CREATE TABLE accounts.user
(
    id            bigint auto_increment NOT NULL,
    country       varchar(30) NOT NULL,
    date_of_birth DATE        NOT NULL,
    first_name    varchar(30) NOT NULL,
    last_name     varchar(30) NOT NULL,
    cell_phone    varchar(20) NOT NULL,
    password      text        not null,
    status        boolean,
    verified      boolean,
    avatar_url    text,
    email         varchar(40) NOT NULL,
    rol_id        BIGINT      NOT null,
    language      varchar(50) NOT NULL,
    CONSTRAINT uq_email UNIQUE (email),
    CONSTRAINT uq_cell_phone UNIQUE (cell_phone),
    CONSTRAINT fk_user_rol_id FOREIGN KEY (rol_id) REFERENCES accounts.rol (id),
    PRIMARY KEY (id)
);

CREATE TABLE accounts.account_game
(
    id         bigint auto_increment NOT NULL,
    server_id  bigint NOT NULL,
    account_id bigint NOT NULL,
    status     boolean,
    user_id    bigint NOT NULL,

    CONSTRAINT fk_account_game_user_id FOREIGN KEY (user_id) REFERENCES accounts.user (id),
    CONSTRAINT uq_server_and_account_and_user UNIQUE (server_id, account_id),
    PRIMARY KEY (id)
);


CREATE TABLE accounts.server
(
    id            bigint auto_increment NOT NULL,
    name          varchar(40) NOT NULL,
    emulator      varchar(40) NOT NULL,
    version       varchar(20) NOT NULL,
    ip            text,
    api_key       varchar(50) NOT NULL,
    api_login     varchar(50) NOT NULL,
    password      varchar(50) NOT NULL,
    avatar_url    text,
    creation_date date,
    web_site      text,
    status        boolean,

    CONSTRAINT uq_email UNIQUE (name, version),
    PRIMARY KEY (id)
);
