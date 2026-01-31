-- Tablas para el minijuego Trivia del bot de Telegram.
-- Ejecutar manualmente si usas ddl-auto: none.

-- Límite diario: 10 preguntas respondidas por usuario por día
CREATE TABLE IF NOT EXISTS trivia_daily_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    count INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_date (user_id, usage_date)
);

-- Límite diario: 10 preguntas creadas por usuario por día
CREATE TABLE IF NOT EXISTS trivia_daily_create (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    count INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_date (user_id, usage_date)
);

-- Si la tabla ya existe, añadir columna: ALTER TABLE trivia_question ADD COLUMN created_by_user_id BIGINT NULL;
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

-- Valoraciones de preguntas (👍/👎). Un voto negativo descuenta 2 puntos al creador.
CREATE TABLE IF NOT EXISTS trivia_question_rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    UNIQUE KEY uk_question_user (question_id, user_id)
);

-- Preguntas de ejemplo (opcional)
-- INSERT INTO trivia_question (question_text, option_a, option_b, option_c, option_d, correct_option, active)
-- VALUES
-- ('¿En qué expansión se introdujo el sistema de talentos?', 'Vanilla', 'The Burning Crusade', 'Wrath of the Lich King', 'Cataclysm', 'A', true),
-- ('¿Cuál es la capital de la Alianza en Azeroth?', 'Orgrimmar', 'Ventormenta', 'Entrañas', 'Cima del Trueno', 'B', true);
