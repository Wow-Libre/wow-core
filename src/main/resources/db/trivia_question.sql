-- Tablas para el minijuego Trivia del bot de Telegram.
-- Ejecutar manualmente si usas ddl-auto: none.

CREATE TABLE IF NOT EXISTS trivia_daily_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    count INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_date (user_id, usage_date)
);

CREATE TABLE IF NOT EXISTS trivia_daily_create (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    count INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_date (user_id, usage_date)
);

CREATE TABLE IF NOT EXISTS trivia_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_text VARCHAR(500) NOT NULL,
    option_a VARCHAR(200) NOT NULL,
    option_b VARCHAR(200) NOT NULL,
    option_c VARCHAR(200) NOT NULL,
    option_d VARCHAR(200) NOT NULL,
    correct_option VARCHAR(1) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by_user_id BIGINT NULL
);

CREATE TABLE IF NOT EXISTS trivia_question_rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    UNIQUE KEY uk_question_user (question_id, user_id)
);
