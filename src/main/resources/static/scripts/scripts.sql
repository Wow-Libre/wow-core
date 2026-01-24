-- Crear el esquema 'platform' si no existe
CREATE SCHEMA IF NOT EXISTS platform;

-- Crear la tabla 'rol'
CREATE TABLE platform.rol
(
    id     bigint AUTO_INCREMENT NOT NULL,
    name   varchar(50) NOT NULL,
    status boolean     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_rol_name UNIQUE (name)
);

INSERT INTO platform.rol (name, status)
VALUES ('ADMIN', TRUE);
INSERT INTO platform.rol (name, status)
VALUES ('CLIENT', TRUE);
INSERT INTO platform.rol (name, status)
VALUES ('SUPPORT', TRUE);

CREATE TABLE platform.user
(
    id            bigint auto_increment NOT NULL,
    country       varchar(30) NOT NULL,
    date_of_birth DATE        NOT NULL,
    first_name    varchar(30) NOT NULL,
    last_name     varchar(30) NOT NULL,
    cell_phone    varchar(20),
    password      text        not null,
    status        boolean,
    verified      boolean,
    avatar_url    text,
    email         varchar(40) NOT NULL,
    rol_id        BIGINT      NOT null,
    language      varchar(10) NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email),
    CONSTRAINT fk_user_rol_id FOREIGN KEY (rol_id) REFERENCES platform.rol (id),
    PRIMARY KEY (id)
);

CREATE TABLE platform.realm
(
    id                bigint auto_increment NOT NULL,
    name              varchar(40) NOT NULL,
    emulator          varchar(40) NOT NULL,
    expansion_id      integer     NOT NULL,
    type              varchar(40) NOT NULL,
    host              text        NOT NULL,
    port              integer     NOT NULL,
    api_key           varchar(80) NOT NULL,
    api_secret        varchar(80) NOT NULL,
    password          text        NOT NULL,
    jwt               text,
    expiration_date   date,
    refresh_token     text,
    avatar_url        text,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    web               text,
    realmlist         varchar(80) NOT NULL,
    status            boolean     NOT NULL,
    external_username varchar(50),
    external_password text        ,
    salt              VARBINARY(16),
    retry             integer,
    disclaimer        varchar(80),
    gm_username varchar(50),
    gm_password text,
    CONSTRAINT uq_realm_name_expansion UNIQUE (name, expansion_id),
    CONSTRAINT uq_realm_api_key UNIQUE (api_key),
    PRIMARY KEY (id)
);

CREATE TABLE platform.account_game
(
    id         bigint auto_increment NOT NULL,
    username   varchar(40) NOT NULL,
    game_email varchar(80) NOT NULL,
    status     boolean,
    account_id bigint      NOT NULL,
    realm_id   bigint      NOT NULL,
    user_id    bigint      NOT NULL,

    CONSTRAINT fk_account_game_user_id FOREIGN KEY (user_id) REFERENCES platform.user (id),
    CONSTRAINT fk_account_game_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id),
    CONSTRAINT uq_account_game_realm_account UNIQUE (realm_id, account_id),
    PRIMARY KEY (id)
);

CREATE TABLE platform.otp_verification
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      text NOT NULL,
    code       text,
    otp        text,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE platform.machine
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT NOT NULL,
    realm_id BIGINT NOT NULL,
    points   INT    NOT NULL,
    last_win TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_coins_user_realm UNIQUE (user_id, realm_id)
);

CREATE TABLE platform.realm_services
(
    id       bigint auto_increment NOT NULL,
    name     ENUM('BANK', 'SEND_LEVEL') NOT NULL,
    amount double NOT NULL,
    realm_id bigint NOT NULL,

    CONSTRAINT fk_realm_services_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id),
    CONSTRAINT uq_realm_services_name_realm UNIQUE (name, realm_id),
    PRIMARY KEY (id)
);

CREATE TABLE platform.realm_resources
(
    id            bigint AUTO_INCREMENT PRIMARY KEY,
    realm_id      bigint NOT NULL,
    resource_type ENUM('HEADER_LEFT', 'HEADER_CENTER', 'HEADER_RIGHT', 'LOGO', 'YOUTUBE_URL') NOT NULL,
    url           TEXT   NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_realm_resources_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id) ON DELETE CASCADE
);

CREATE TABLE platform.realm_events
(
    id          bigint AUTO_INCREMENT PRIMARY KEY,
    img         text         NOT NULL,
    title       VARCHAR(50)  NOT NULL,
    description VARCHAR(120) NOT NULL,
    disclaimer  varchar(200) NOT NULL,
    realm_id    bigint       NOT NULL,
    CONSTRAINT fk_realm_events_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id) ON DELETE CASCADE
);

CREATE TABLE platform.realm_details
(
    id         bigint AUTO_INCREMENT PRIMARY KEY,
    realm_id   bigint      NOT NULL,
    `key`      VARCHAR(30) NOT NULL,
    `value`    VARCHAR(80) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_realm_details_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id) ON DELETE CASCADE
);

CREATE TABLE platform.credit_loans
(
    id               bigint auto_increment NOT NULL,
    account_game_id  bigint                              NOT NULL,
    character_id     bigint,
    realm_id         bigint,
    reference_serial varchar(60)                         NOT NULL,
    amount_transferred double NOT NULL,
    debt_to_pay double NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    payment_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    interests        INTEGER                             NOT NULL,
    status           boolean                             NOT NULL,
    send             boolean                             NOT NULL,

    CONSTRAINT uq_credit_loans_reference UNIQUE (reference_serial),
    CONSTRAINT fk_credit_loans_account_game_id FOREIGN KEY (account_game_id) REFERENCES platform.account_game (id),
    PRIMARY KEY (id)
);

CREATE TABLE platform.benefit_guild
(
    id         bigint auto_increment NOT NULL,
    realm_id   bigint      NOT NULL,
    guild_name varchar(70) NOT NULL,
    guild_id   bigint      NOT NULL,
    benefit_id bigint      NOT NULL,
    status     boolean     NOT NULL,
    CONSTRAINT uq_benefit_guild_realm_guild_benefit UNIQUE (realm_id, guild_id, benefit_id),
    CONSTRAINT fk_benefit_guild_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id),
    PRIMARY KEY (id)
);

CREATE TABLE platform.character_benefit_guild
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    account_id       BIGINT NOT NULL,
    character_id     BIGINT NOT NULL,
    benefit_guild_id BIGINT NOT NULL,
    benefit_send     BOOLEAN,

    CONSTRAINT uq_character_benefit_guild UNIQUE (character_id, account_id, benefit_guild_id),
    CONSTRAINT fk_character_benefit_guild_id FOREIGN KEY (benefit_guild_id) REFERENCES platform.benefit_guild (id),
    PRIMARY KEY (id)
);

CREATE TABLE platform.promotion
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference       varchar(40) NOT NULL,
    img_url         text        NOT NULL,
    name            varchar(30) NOT NULL,
    description     varchar(80) NOT NULL,
    btn_text        varchar(30) NOT NULL,
    send_item       boolean     NOT NULL DEFAULT FALSE,
    realm_id        bigint      NOT NULL,
    min_level       integer     NOT NULL,
    max_level       integer     NOT NULL,
    type            varchar(30) NOT NULL,
    amount double NOT NULL,
    class_character varchar(10) NOT NULL,
    level           integer,
    status          boolean     NOT NULL,
    language        varchar(2)  NOT NULL,
    CONSTRAINT uq_promotion_reference UNIQUE (reference)
);

CREATE TABLE platform.user_promotion
(
    id              bigint AUTO_INCREMENT NOT NULL,
    account_game_id bigint                              NOT NULL,
    character_id    bigint                              NOT NULL,
    promotion_id    bigint                              NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    realm_id        bigint                              NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_user_promotion_user_account_promo_realm UNIQUE (character_id,account_game_id, promotion_id, realm_id),
    CONSTRAINT fk_user_promotion_promotion_id FOREIGN KEY (promotion_id) REFERENCES platform.promotion (id),
    CONSTRAINT fk_user_promotion_account_game_id FOREIGN KEY (account_game_id) REFERENCES platform.account_game (id)
);

CREATE TABLE platform.promotion_item
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         varchar(30) NOT NULL,
    quantity     integer     NOT NULL,
    promotion_id bigint      NOT NULL,
    CONSTRAINT fk_promotion_item_promotion_id FOREIGN KEY (promotion_id) references platform.promotion (id)
);

CREATE TABLE platform.teleport
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    img_url text NOT NULL,
    name    VARCHAR(80) NOT NULL,
    position_x DOUBLE NOT NULL,
    position_y DOUBLE NOT NULL,
    position_z DOUBLE NOT NULL ,
    map     INT NOT NULL,
    orientation DOUBLE NOT NULL,
    zone    INT NOT NULL,
    area DOUBLE,
    realm_id BIGINT NOT NULL,
    faction ENUM('HORDE', 'ALLIANCE','ALL') NOT NULL,
    CONSTRAINT fk_teleport_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id),
    CONSTRAINT uq_teleport_name_realm UNIQUE (name, realm_id)
);

CREATE TABLE platform.faqs
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    question   TEXT         NOT NULL,
    answer     TEXT         NOT NULL,
    language   VARCHAR(255) NOT NULL,
    type ENUM('SUPPORT', 'SUBSCRIPTION') NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL
);

CREATE TABLE platform.realm_advertising (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag VARCHAR(10) NOT NULL,
    sub_title VARCHAR(40) NOT NULL,
    description TEXT,
    cta_primary VARCHAR(20) NOT NULL,
    img_url TEXT NOT NULL,
    footer_disclaimer VARCHAR(40) NOT NULL,
    language VARCHAR(10) NOT NULL,
    realm_id BIGINT NOT NULL,
    CONSTRAINT fk_realm_advertising_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm(id)
);

CREATE TABLE IF NOT EXISTS platform.news (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(80) NOT NULL,
  sub_title VARCHAR(80) NOT NULL,
  img_url TEXT,
  author VARCHAR(50),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS platform.news_sections (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  img_url TEXT,
  news_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  section_order INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_news_sections_news_id FOREIGN KEY (news_id)
    REFERENCES news(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE platform.notification_providers (
   id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
   type ENUM('MAILS','METRICS') NOT NULL,
   host VARCHAR(100) NOT NULL,
   client VARCHAR(100),
   secret_key TEXT,
   enabled BOOLEAN DEFAULT true,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   CONSTRAINT uq_notification_providers_provider_type UNIQUE (type)
);


CREATE TABLE platform.banners (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  media_url TEXT NOT NULL,
  alt TEXT,
  language VARCHAR(5) NOT NULL,
  type ENUM('IMAGE', 'VIDEO') NOT null,
  label VARCHAR(80)
);


CREATE TABLE IF NOT EXISTS platform.voting_platforms (
  id BIGINT NOT NULL AUTO_INCREMENT,
  img_url text NOT NULL,
  name VARCHAR(80) NOT NULL,
  postback_url VARCHAR(90) NOT NULL,
  allowed_host VARCHAR(80),
  is_active boolean  NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uq_voting_platforms_name UNIQUE (name, postback_url)
);


CREATE TABLE platform.vote_wallet (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    vote_balance INT NOT NULL,
    total_votes INT NOT NULL,
    ip_address VARCHAR(80),
    reference_code TEXT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_vote_wallet_user_id FOREIGN KEY (user_id) REFERENCES platform.user(id),
    CONSTRAINT fk_vote_wallet_platform_id FOREIGN KEY (platform_id) REFERENCES platform.voting_platforms(id),
    CONSTRAINT uq_vote_wallet_user_platform UNIQUE (user_id, platform_id)
);

CREATE TABLE platform.interstitial (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    url_img TEXT NOT NULL,
    redirect_url TEXT NOT NULL,
    status BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE platform.interstitial_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    views BIGINT,
    interstitial_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    viewed_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_interstitial_user_user_id FOREIGN KEY (user_id) REFERENCES platform.user(id),
    CONSTRAINT fk_interstitial_user_interstitial_id FOREIGN KEY (interstitial_id) REFERENCES platform.interstitial(id)
);

CREATE TABLE platform.wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    points BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallets_user_id
    FOREIGN KEY (user_id) REFERENCES platform.user(id)
);


CREATE TABLE platform.plans
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    name            VARCHAR(50) NOT NULL,
    price           DOUBLE NOT NULL,
    price_title     varchar(30) NOT NULL,
    description     text,
    discount        INTEGER     NOT NULL,
    status          BOOLEAN     NOT NULL,
    currency        VARCHAR(4)  NOT NULL,
    frequency_type  VARCHAR(30),
    frequency_value INTEGER,
    free_trial_days INTEGER,
    created_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    tax             VARCHAR(50) NOT NULL,
    return_tax      VARCHAR(50) NOT NULL,
    features JSON NULL,
    language        VARCHAR(20),
    PRIMARY KEY (id)
);


CREATE TABLE platform.subscriptions
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    user_id           BIGINT      NOT NULL,
    plan_id           BIGINT      NOT NULL,
    next_invoice_date DATE        NOT NULL,
    reference_number  VARCHAR(80) NOT NULL,
    status            VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT subscriptions_reference_number_uq UNIQUE (reference_number),
    CONSTRAINT fk_subscriptions_user_id FOREIGN KEY (user_id) REFERENCES platform.user(id)
);
CREATE TABLE platform.product_category
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(50) NOT NULL,
    description TEXT,
    disclaimer  TEXT,
    PRIMARY KEY (id),
    CONSTRAINT product_category_name_uq UNIQUE (name)
);

CREATE TABLE platform.products
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    name                    VARCHAR(50) NOT NULL,
    product_category_id     BIGINT      NOT NULL,
    disclaimer              TEXT,
    price                   DOUBLE NOT NULL,
    discount                INTEGER,
    description             TEXT,
    image_url               TEXT        NOT NULL,
    realm_id                BIGINT      NOT NULL,
    realm_name              varchar(80) NOT NULL,
    reference_number        VARCHAR(80) NOT NULL,
    status                  BOOLEAN     NOT NULL,
    credit_points_enabled   BOOLEAN     NOT NULL,
    credit_points_amount    BIGINT,
    tax                     VARCHAR(50) NOT NULL,
    return_tax              VARCHAR(50) NOT NULL,
    language                VARCHAR(20) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT products_name_and_realm_id_and_language_uq UNIQUE (name,realm_id,language),
    CONSTRAINT products_reference_number_uq UNIQUE (reference_number),
    CONSTRAINT products_product_category_id_fk FOREIGN KEY (product_category_id) REFERENCES platform.product_category (id)
);

CREATE TABLE platform.product_details
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    product_id  BIGINT      NOT NULL,
    title       VARCHAR(60) NOT NULL,
    description TEXT        NOT NULL,
    img_url     TEXT        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT product_details_product_id FOREIGN KEY (product_id) REFERENCES platform.products (id)
);

CREATE TABLE platform.packages
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    code_core  VARCHAR(50) NOT NULL,
    product_id BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT packages_product_id FOREIGN KEY (product_id) REFERENCES platform.products (id)
);


CREATE TABLE platform.transactions
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    user_id           BIGINT      NOT NULL,
    account_id        BIGINT,
    realm_id         BIGINT,
    price             DOUBLE NOT NULL,
    status            VARCHAR(50) NOT NULL,
    product_id        BIGINT NOT NULL,
    subscription_id   BIGINT NOT NULL,
    plan_id         BIGINT,
    reference_number  VARCHAR(80) NOT NULL,
    creation_date     DATETIME(6) NOT NULL,
    payment_method    VARCHAR(60),
    credit_points     BOOLEAN NOT NULL,
    currency          VARCHAR(20) NOT NULL,
    send              BOOLEAN     NOT NULL,
    reference_payment varchar (150),
    is_subscription   BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT transactions_user_id_fk FOREIGN key (user_id)  REFERENCES platform.user (id),
    CONSTRAINT transactions_product_reference_uq UNIQUE (reference_number),
    CONSTRAINT transactions_product_id FOREIGN KEY (product_id) REFERENCES platform.products (id),
    CONSTRAINT transactions_subscriptions_id_fk FOREIGN KEY (subscription_id) REFERENCES platform.subscriptions (id)

);


CREATE TABLE platform.subscription_benefits
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    user_id    BIGINT NOT NULL,
    benefit_id BIGINT NOT NULL,
    created_at DATE   NOT NULL,
    realm_id   BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT subscription_benefits_uq UNIQUE (user_id, benefit_id),
    CONSTRAINT subscription_benefits_user_fk FOREIGN key (user_id)  REFERENCES platform.user (id)
);




CREATE TABLE platform.payment_gateways (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(100) NOT NULL,
                                           type ENUM('STRIPE', 'PAYU') NOT NULL,
                                           is_active BOOLEAN DEFAULT TRUE,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           CONSTRAINT unique_type UNIQUE (type)
);

CREATE TABLE platform.payu_credentials (
                                           gateway_id BIGINT PRIMARY KEY,
                                           host VARCHAR(255) NOT NULL,
                                           api_key VARCHAR(255) NOT NULL,
                                           api_login VARCHAR(255) NOT NULL,
                                           key_public VARCHAR(255) NOT NULL,
                                           success_url VARCHAR(255) NOT NULL,
                                           cancel_url VARCHAR(255),
                                           webhook_url VARCHAR(255) NOT NULL,
                                           merchant_id VARCHAR(255) NOT NULL,
                                           account_id VARCHAR(255) NOT NULL,
                                           FOREIGN KEY (gateway_id) REFERENCES payment_gateways(id) ON DELETE CASCADE
);

CREATE TABLE platform.stripe_credentials (
                                             gateway_id BIGINT NOT NULL PRIMARY KEY,
                                             api_secret VARCHAR(255) NOT NULL,
                                             api_public VARCHAR(255) NOT NULL,
                                             success_url VARCHAR(255) NOT NULL,
                                             cancel_url VARCHAR(255) NOT NULL,
                                             webhook_url VARCHAR(255) NOT NULL,
                                             webhook_secret VARCHAR(255) NOT NULL,
                                             CONSTRAINT fk_stripe_credentials_gateway FOREIGN KEY (gateway_id) REFERENCES payment_gateways(id) ON DELETE CASCADE
);


CREATE TABLE platform.`benefit_premiums` (
                                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                                            `img` VARCHAR(255) DEFAULT NULL,
                                            `name` VARCHAR(255) NOT NULL,
                                            `description` TEXT DEFAULT NULL,
                                            `command` VARCHAR(255) DEFAULT NULL,
                                            `send_item` TINYINT(1) NOT NULL DEFAULT 0,
                                            `reactivable` TINYINT(1) NOT NULL DEFAULT 0,
                                            amount DOUBLE NOT NULL,
                                            `btn_text` VARCHAR(255) DEFAULT NULL,
                                            `type` VARCHAR(100) DEFAULT NULL,
                                            `realm_id` BIGINT DEFAULT NULL,
                                            `language` VARCHAR(20) DEFAULT NULL,
                                            `status` TINYINT(1) NOT NULL DEFAULT 1,
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE  platform.`benefit_premium_items` (
                                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                  `benefit_premium_id` BIGINT NOT NULL,
                                                  `code` VARCHAR(255) NOT NULL,
                                                  `quantity` INT NOT NULL,
                                                  PRIMARY KEY (`id`),
                                                  CONSTRAINT `fk_benefit_premium_item_benefit_premium` FOREIGN KEY (`benefit_premium_id`) REFERENCES `benefit_premium` (`id`)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
