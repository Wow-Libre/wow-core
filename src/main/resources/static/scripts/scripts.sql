-- Crear el esquema 'platform' si no existe
create SCHEMA IF NOT exists platform;

-- Crear la tabla 'rol'
create table platform.rol (
                              id BIGINT AUTO_INCREMENT NOT null,
                              name VARCHAR(50) NOT null,
                              status BOOLEAN NOT null,
                              primary key (id),
                              constraint uq_rol_name unique (name)
);

insert into
    platform.rol (name, status)
values
    ('ADMIN', true);

insert into
    platform.rol (name, status)
values
    ('CLIENT', true);

insert into
    platform.rol (name, status)
values
    ('SUPPORT', true);

create table platform.user (
                               id BIGINT auto_increment NOT null,
                               country VARCHAR(30) NOT null,
                               date_of_birth DATE NOT null,
                               first_name VARCHAR(30) NOT null,
                               last_name VARCHAR(30) NOT null,
                               cell_phone VARCHAR(20),
                               password TEXT NOT null,
                               status BOOLEAN,
                               verified BOOLEAN,
                               avatar_url TEXT,
                               email VARCHAR(40) NOT null,
                               rol_id BIGINT NOT null,
                               LANGUAGE VARCHAR(10) NOT null,
                               constraint uq_user_email unique (email),
                               constraint fk_user_rol_id foreign key (rol_id) references platform.rol (id),
                               primary key (id)
);

create table platform.realm (
                                id BIGINT auto_increment NOT null,
                                name VARCHAR(40) NOT null,
                                emulator VARCHAR(40) NOT null,
                                expansion_id INTEGER NOT null,
                                type VARCHAR(40) NOT null,
                                host TEXT NOT null,
                                port INTEGER NOT null,
                                api_key VARCHAR(80) NOT null,
                                api_secret VARCHAR(80) NOT null,
                                password TEXT NOT null,
                                jwt TEXT,
                                expiration_date DATE,
                                refresh_token TEXT,
                                avatar_url TEXT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                web TEXT,
                                realmlist VARCHAR(80) NOT null,
                                status BOOLEAN NOT null,
                                external_username VARCHAR(50),
                                external_password TEXT,
                                salt VARBINARY(16),
                                retry INTEGER,
                                disclaimer VARCHAR(80),
                                gm_username VARCHAR(50),
                                gm_password TEXT,
                                constraint uq_realm_name_expansion unique (name, expansion_id),
                                constraint uq_realm_api_key unique (api_key),
                                primary key (id)
);

create table platform.account_game (
                                       id BIGINT auto_increment NOT null,
                                       username VARCHAR(40) NOT null,
                                       game_email VARCHAR(80) NOT null,
                                       status BOOLEAN,
                                       account_id BIGINT NOT null,
                                       realm_id BIGINT NOT null,
                                       user_id BIGINT NOT null,
                                       constraint fk_account_game_user_id foreign key (user_id) references platform.user (id),
                                       constraint fk_account_game_realm_id foreign key (realm_id) references platform.realm (id),
                                       constraint uq_account_game_realm_account unique (realm_id, account_id),
                                       primary key (id)
);

create table platform.otp_verification (
                                           id BIGINT AUTO_INCREMENT primary key,
                                           email TEXT NOT null,
                                           code TEXT,
                                           otp TEXT,
                                           created_at TIMESTAMP DEFAULT NOW()
);

create table platform.machine (
                                  id BIGINT AUTO_INCREMENT primary key,
                                  user_id BIGINT NOT null,
                                  realm_id BIGINT NOT null,
                                  points INT NOT null,
                                  last_win TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  constraint uq_coins_user_realm unique (user_id, realm_id)
);

create table platform.realm_services (
                                         id BIGINT auto_increment NOT null,
                                         name ENUM('BANK', 'SEND_LEVEL') NOT null,
                                         amount DOUBLE NOT null,
                                         realm_id BIGINT NOT null,
                                         constraint fk_realm_services_realm_id foreign key (realm_id) references platform.realm (id),
                                         constraint uq_realm_services_name_realm unique (name, realm_id),
                                         primary key (id)
);

create table platform.realm_resources (
                                          id BIGINT AUTO_INCREMENT primary key,
                                          realm_id BIGINT NOT null,
                                          resource_type ENUM(
    'HEADER_LEFT',
    'HEADER_CENTER',
    'HEADER_RIGHT',
    'LOGO',
    'YOUTUBE_URL'
  ) NOT null,
                                          url TEXT NOT null,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
                                          constraint fk_realm_resources_realm_id foreign key (realm_id) references platform.realm (id) on delete cascade
);

create table platform.realm_events (
                                       id BIGINT AUTO_INCREMENT primary key,
                                       img TEXT NOT null,
                                       title VARCHAR(50) NOT null,
                                       description VARCHAR(120) NOT null,
                                       disclaimer VARCHAR(200) NOT null,
                                       realm_id BIGINT NOT null,
                                       constraint fk_realm_events_realm_id foreign key (realm_id) references platform.realm (id) on delete cascade
);

create table platform.realm_details (
                                        id BIGINT AUTO_INCREMENT primary key,
                                        realm_id BIGINT NOT null,
                                        `key` VARCHAR(30) NOT null,
                                        `value` VARCHAR(80) NOT null,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        constraint fk_realm_details_realm_id foreign key (realm_id) references platform.realm (id) on delete cascade
);

create table platform.credit_loans (
                                       id BIGINT auto_increment NOT null,
                                       account_game_id BIGINT NOT null,
                                       character_id BIGINT,
                                       realm_id BIGINT,
                                       reference_serial VARCHAR(60) NOT null,
                                       amount_transferred DOUBLE NOT null,
                                       debt_to_pay DOUBLE NOT null,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
                                       payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
                                       interests INTEGER NOT null,
                                       status BOOLEAN NOT null,
                                       send BOOLEAN NOT null,
                                       constraint uq_credit_loans_reference unique (reference_serial),
                                       constraint fk_credit_loans_account_game_id foreign key (account_game_id) references platform.account_game (id),
                                       primary key (id)
);


create table platform.promotion (
                                    id BIGINT AUTO_INCREMENT primary key,
                                    reference VARCHAR(40) NOT null,
                                    img_url TEXT NOT null,
                                    name VARCHAR(30) NOT null,
                                    description VARCHAR(80) NOT null,
                                    btn_text VARCHAR(30) NOT null,
                                    send_item BOOLEAN NOT null DEFAULT false,
                                    realm_id BIGINT NOT null,
                                    min_level INTEGER NOT null,
                                    max_level INTEGER NOT null,
                                    type VARCHAR(30) NOT null,
                                    amount DOUBLE NOT null,
                                    class_character VARCHAR(10) NOT null,
                                    level INTEGER,
                                    status BOOLEAN NOT null,
                                    LANGUAGE VARCHAR(2) NOT null,
                                    constraint uq_promotion_reference unique (reference)
);

create table platform.user_promotion (
                                         id BIGINT AUTO_INCREMENT NOT null,
                                         account_game_id BIGINT NOT null,
                                         character_id BIGINT NOT null,
                                         promotion_id BIGINT NOT null,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
                                         realm_id BIGINT NOT null,
                                         primary key (id),
                                         constraint uq_user_promotion_user_account_promo_realm unique (
                                                                                                       character_id,
                                                                                                       account_game_id,
                                                                                                       promotion_id,
                                                                                                       realm_id
                                             ),
                                         constraint fk_user_promotion_promotion_id foreign key (promotion_id) references platform.promotion (id),
                                         constraint fk_user_promotion_account_game_id foreign key (account_game_id) references platform.account_game (id)
);

create table platform.promotion_item (
                                         id BIGINT AUTO_INCREMENT primary key,
                                         code VARCHAR(30) NOT null,
                                         quantity INTEGER NOT null,
                                         promotion_id BIGINT NOT null,
                                         constraint fk_promotion_item_promotion_id foreign key (promotion_id) references platform.promotion (id)
);

create table platform.teleport (
                                   id BIGINT AUTO_INCREMENT primary key,
                                   img_url TEXT NOT null,
                                   name VARCHAR(80) NOT null,
                                   position_x DOUBLE NOT null,
                                   position_y DOUBLE NOT null,
                                   position_z DOUBLE NOT null,
                                   map INT NOT null,
                                   orientation DOUBLE NOT null,
                                   zone INT NOT null,
                                   area DOUBLE,
                                   realm_id BIGINT NOT null,
                                   faction ENUM('HORDE', 'ALLIANCE', 'ALL') NOT null,
                                   constraint fk_teleport_realm_id foreign key (realm_id) references platform.realm (id),
                                   constraint uq_teleport_name_realm unique (name, realm_id)
);

create table platform.faqs (
                               id BIGINT AUTO_INCREMENT primary key,
                               question TEXT NOT null,
                               answer TEXT NOT null,
                               LANGUAGE VARCHAR(255) NOT null,
                               type ENUM('SUPPORT', 'SUBSCRIPTION') NOT null,
                               created_at DATETIME NOT null,
                               updated_at DATETIME NOT null
);

create table platform.realm_advertising (
                                            id BIGINT AUTO_INCREMENT primary key,
                                            tag VARCHAR(10) NOT null,
                                            sub_title VARCHAR(40) NOT null,
                                            description TEXT,
                                            cta_primary VARCHAR(20) NOT null,
                                            img_url TEXT NOT null,
                                            footer_disclaimer VARCHAR(40) NOT null,
                                            LANGUAGE VARCHAR(10) NOT null,
                                            realm_id BIGINT NOT null,
                                            constraint fk_realm_advertising_realm_id foreign key (realm_id) references platform.realm (id)
);

create table if not exists platform.news (
                                             id BIGINT NOT null AUTO_INCREMENT,
                                             title VARCHAR(80) NOT null,
    sub_title VARCHAR(80) NOT null,
    img_url TEXT,
    author VARCHAR(50),
    created_at DATETIME NOT null DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    primary key (id)
    );

create table if not exists platform.news_sections (
                                                      id BIGINT NOT null AUTO_INCREMENT,
                                                      title VARCHAR(100) NOT null,
    img_url TEXT,
    news_id BIGINT NOT null,
    content TEXT NOT null,
    section_order INT NOT null,
    primary key (id),
    constraint fk_news_sections_news_id foreign key (news_id) references news (id) on delete cascade on update cascade
    );

create table platform.notification_providers (
                                                 id BIGINT NOT null primary key AUTO_INCREMENT,
                                                 type ENUM('MAILS', 'METRICS') NOT null,
                                                 host VARCHAR(100) NOT null,
                                                 client VARCHAR(100),
                                                 secret_key TEXT,
                                                 enabled BOOLEAN DEFAULT true,
                                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
                                                 constraint uq_notification_providers_provider_type unique (type)
);

create table platform.banners (
                                  id BIGINT NOT null primary key AUTO_INCREMENT,
                                  media_url TEXT NOT null,
                                  alt TEXT,
                                  LANGUAGE VARCHAR(5) NOT null,
                                  type ENUM('IMAGE', 'VIDEO') NOT null,
                                  label VARCHAR(80)
);

create table if not exists platform.voting_platforms (
                                                         id BIGINT NOT null AUTO_INCREMENT,
                                                         img_url TEXT NOT null,
                                                         name VARCHAR(80) NOT null,
    postback_url VARCHAR(90) NOT null,
    allowed_host VARCHAR(80),
    is_active BOOLEAN NOT null,
    created_at DATETIME NOT null DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    primary key (id),
    constraint uq_voting_platforms_name unique (name, postback_url)
    );

create table platform.vote_wallet (
                                      id BIGINT NOT null AUTO_INCREMENT primary key,
                                      user_id BIGINT NOT null,
                                      platform_id BIGINT NOT null,
                                      vote_balance INT NOT null,
                                      total_votes INT NOT null,
                                      ip_address VARCHAR(80),
                                      reference_code TEXT NOT null,
                                      created_at DATETIME,
                                      updated_at DATETIME,
                                      constraint fk_vote_wallet_user_id foreign key (user_id) references platform.user (id),
                                      constraint fk_vote_wallet_platform_id foreign key (platform_id) references platform.voting_platforms (id),
                                      constraint uq_vote_wallet_user_platform unique (user_id, platform_id)
);

create table platform.interstitial (
                                       id BIGINT NOT null AUTO_INCREMENT primary key,
                                       url_img TEXT NOT null,
                                       redirect_url TEXT NOT null,
                                       status BOOLEAN NOT null,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
);

create table platform.interstitial_user (
                                            id BIGINT NOT null AUTO_INCREMENT primary key,
                                            user_id BIGINT NOT null,
                                            views BIGINT,
                                            interstitial_id BIGINT NOT null,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            viewed_at TIMESTAMP NOT null,
                                            constraint fk_interstitial_user_user_id foreign key (user_id) references platform.user (id),
                                            constraint fk_interstitial_user_interstitial_id foreign key (interstitial_id) references platform.interstitial (id)
);

create table platform.wallets (
                                  id BIGINT AUTO_INCREMENT primary key,
                                  user_id BIGINT NOT null,
                                  points BIGINT NOT null,
                                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
                                  constraint fk_wallets_user_id constraint uq_wallets_user_id unique (user_id),
                                  foreign key (user_id) references platform.user (id)
);

create table platform.plans (
                                id BIGINT AUTO_INCREMENT NOT null,
                                name VARCHAR(50) NOT null,
                                price DOUBLE NOT null,
                                price_title VARCHAR(30) NOT null,
                                description TEXT,
                                discount INTEGER NOT null,
                                status BOOLEAN NOT null,
                                currency VARCHAR(4) NOT null,
                                frequency_type VARCHAR(30),
                                frequency_value INTEGER,
                                free_trial_days INTEGER,
                                created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
                                updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) on update CURRENT_TIMESTAMP(6),
                                tax VARCHAR(50) NOT null,
                                return_tax VARCHAR(50) NOT null,
                                features JSON null,
                                LANGUAGE VARCHAR(20),
                                primary key (id)
);

create table platform.subscriptions (
                                        id BIGINT AUTO_INCREMENT NOT null,
                                        user_id BIGINT NOT null,
                                        plan_id BIGINT NOT null,
                                        next_invoice_date DATE NOT null,
                                        reference_number VARCHAR(80) NOT null,
                                        status VARCHAR(50) NOT null,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
                                        primary key (id),
                                        constraint subscriptions_reference_number_uq unique (reference_number),
                                        constraint fk_subscriptions_user_id foreign key (user_id) references platform.user (id)
);

create table platform.product_category (
                                           id BIGINT AUTO_INCREMENT NOT null,
                                           name VARCHAR(50) NOT null,
                                           description TEXT,
                                           disclaimer TEXT,
                                           primary key (id),
                                           constraint product_category_name_uq unique (name)
);

create table platform.products (
                                   id BIGINT AUTO_INCREMENT NOT null,
                                   name VARCHAR(50) NOT null,
                                   product_category_id BIGINT NOT null,
                                   disclaimer TEXT,
                                   price DOUBLE NOT null,
                                   discount INTEGER,
                                   description TEXT,
                                   image_url TEXT NOT null,
                                   realm_id BIGINT NOT null,
                                   realm_name VARCHAR(80) NOT null,
                                   reference_number VARCHAR(80) NOT null,
                                   status BOOLEAN NOT null,
                                   credit_points_enabled BOOLEAN NOT null,
                                   credit_points_amount BIGINT,
                                   tax VARCHAR(50) NOT null,
                                   return_tax VARCHAR(50) NOT null,
                                   LANGUAGE VARCHAR(20) NOT null,
                                   primary key (id),
                                   constraint products_name_and_realm_id_and_language_uq unique (name, realm_id, LANGUAGE),
                                   constraint products_reference_number_uq unique (reference_number),
                                   constraint products_product_category_id_fk foreign key (product_category_id) references platform.product_category (id)
);

create table platform.product_details (
                                          id BIGINT AUTO_INCREMENT NOT null,
                                          product_id BIGINT NOT null,
                                          title VARCHAR(60) NOT null,
                                          description TEXT NOT null,
                                          img_url TEXT NOT null,
                                          primary key (id),
                                          constraint product_details_product_id foreign key (product_id) references platform.products (id)
);

create table platform.packages (
                                   id BIGINT AUTO_INCREMENT NOT null,
                                   code_core VARCHAR(50) NOT null,
                                   product_id BIGINT NOT null,
                                   primary key (id),
                                   constraint packages_product_id foreign key (product_id) references platform.products (id)
);

create table platform.transactions (
                                       id BIGINT AUTO_INCREMENT NOT null,
                                       user_id BIGINT NOT null,
                                       account_id BIGINT,
                                       realm_id BIGINT,
                                       price DOUBLE NOT null,
                                       status VARCHAR(50) NOT null,
                                       product_id BIGINT,
                                       subscription_id BIGINT,
                                       plan_id BIGINT,
                                       reference_number VARCHAR(80) NOT null,
                                       creation_date DATETIME(6) NOT null,
                                       payment_method VARCHAR(60),
                                       credit_points BOOLEAN NOT null,
                                       currency VARCHAR(20) NOT null,
                                       send BOOLEAN NOT null,
                                       reference_payment VARCHAR(150),
                                       is_subscription BOOLEAN NOT null,
                                       primary key (id),
                                       constraint transactions_user_id_fk foreign key (user_id) references platform.user (id),
                                       constraint transactions_product_reference_uq unique (reference_number),
                                       constraint transactions_product_id foreign key (product_id) references platform.products (id),
                                       constraint transactions_subscriptions_id_fk foreign key (subscription_id) references platform.subscriptions (id)
);

create table platform.subscription_benefits (
                                                id BIGINT AUTO_INCREMENT NOT null,
                                                user_id BIGINT NOT null,
                                                benefit_id BIGINT NOT null,
                                                created_at DATE NOT null,
                                                realm_id BIGINT NOT null,
                                                primary key (id),
                                                constraint subscription_benefits_uq unique (user_id, benefit_id),
                                                constraint subscription_benefits_user_fk foreign key (user_id) references platform.user (id)
);

create table platform.payment_gateways (
                                           id BIGINT AUTO_INCREMENT primary key,
                                           name VARCHAR(100) NOT null,
                                           type ENUM('STRIPE', 'PAYU') NOT null,
                                           is_active BOOLEAN DEFAULT true,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           constraint unique_type unique (type)
);

create table platform.payu_credentials (
                                           gateway_id BIGINT primary key,
                                           host VARCHAR(255) NOT null,
                                           api_key VARCHAR(255) NOT null,
                                           api_login VARCHAR(255) NOT null,
                                           key_public VARCHAR(255) NOT null,
                                           success_url VARCHAR(255) NOT null,
                                           cancel_url VARCHAR(255),
                                           webhook_url VARCHAR(255) NOT null,
                                           merchant_id VARCHAR(255) NOT null,
                                           account_id VARCHAR(255) NOT null,
                                           foreign key (gateway_id) references payment_gateways (id) on delete cascade
);

create table platform.stripe_credentials (
                                             gateway_id BIGINT NOT null primary key,
                                             api_secret VARCHAR(255) NOT null,
                                             api_public VARCHAR(255) NOT null,
                                             success_url VARCHAR(255) NOT null,
                                             cancel_url VARCHAR(255) NOT null,
                                             webhook_url VARCHAR(255) NOT null,
                                             webhook_secret VARCHAR(255) NOT null,
                                             constraint fk_stripe_credentials_gateway foreign key (gateway_id) references payment_gateways (id) on delete cascade
);

CREATE TABLE platform.`benefit_premiums` (
                                             `id` BIGINT NOT null AUTO_INCREMENT,
                                             `img` VARCHAR(255) DEFAULT null,
                                             `name` VARCHAR(255) NOT null,
                                             `description` TEXT DEFAULT null,
                                             `command` VARCHAR(255) DEFAULT null,
                                             `send_item` TINYINT(1) NOT null DEFAULT 0,
                                             `reactivable` TINYINT(1) NOT null DEFAULT 0,
                                             amount DOUBLE NOT null,
                                             `btn_text` VARCHAR(255) DEFAULT null,
                                             `type` VARCHAR(100) DEFAULT null,
                                             `realm_id` BIGINT DEFAULT null,
                                             `language` VARCHAR(20) DEFAULT null,
                                             `status` TINYINT(1) NOT null DEFAULT 1,
                                             primary key (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE platform.benefit_premium_items (
       id BIGINT  AUTO_INCREMENT NOT NULL PRIMARY KEY
       benefit_premium_id BIGINT NOT NULL,
       code VARCHAR(255) NOT NULL,
       quantity INT NOT NULL,
       constraint fk_benefit_premium_item_benefit_premiums foreign key (benefit_premium_id) references benefit_premiums (id) on delete cascade on update cascade
);


/* EN: Table to manage guild benefits catalog and assignments to guilds and characters
   ES: Tabla para gestionar el catálogo de beneficios del gremio y las asignaciones a gremios y personajes
   PR: Tabela para gerenciar o catálogo de benefícios da guilda e as alocações para guildas e personagens.
*/
CREATE TABLE platform.guild_benefits_catalog(
        id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
        title varchar (80) NOT NULL,
        subtitle varchar (80) NOT NULL,
        description varchar (120) NOT NULL,
        image_url text NOT NULL,
        core_code varchar (20) NOT NULL,
        quantity integer NOT NULL,
        is_active BOOLEAN DEFAULT true,
        external_url text NOT NULL,
        language VARCHAR(10) NOT NULL
);


CREATE TABLE platform.guild_benefits (
         id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
         realm_id BIGINT NOT NULL,
         guild_id BIGINT NOT NULL,
         guild_benefit_catalog_id BIGINT NOT NULL,
         CONSTRAINT uq_benefit_guild_realm_guild_benefit UNIQUE (realm_id, guild_id, guild_benefit_catalog_id),
         CONSTRAINT fk_benefit_guild_realm_id FOREIGN KEY (realm_id) REFERENCES platform.realm (id),
         CONSTRAINT fk_benefit_guild_guild_benefit_catalog_id FOREIGN KEY (guild_benefit_catalog_id) REFERENCES platform.guild_benefits_catalog (id)
);

CREATE TABLE platform.character_benefit_guild (
        id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
        account_id BIGINT NOT NULL,
        character_id BIGINT NOT NULL,
        guild_benefits_id BIGINT NOT NULL,
        benefit_send BOOLEAN,
        CONSTRAINT uq_character_benefit_guild UNIQUE (character_id, account_id, guild_benefits_id),
        CONSTRAINT fk_character_guild_benefits_id FOREIGN KEY (guild_benefits_id) REFERENCES platform.guild_benefits (id)
);


