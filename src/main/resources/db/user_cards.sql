-- Cartas descubiertas por usuario.
-- Relación: card_catalog define las cartas que existen (code, image_url, display_name).
--           user_cards guarda qué cartas tiene cada usuario (user_id + card_code).
-- Si el usuario ya tiene la carta: UNIQUE(user_id, card_code) impide duplicados.
-- Al dar una carta: INSERT y si ya existe (duplicate key) ignorar o actualizar obtained_at.
-- Ejecutar DESPUÉS de card_catalog.sql (hay FK card_code -> card_catalog.code).
-- Si user_cards ya existía sin FK: ALTER TABLE platform.user_cards ADD CONSTRAINT fk_user_cards_catalog FOREIGN KEY (card_code) REFERENCES platform.card_catalog(code) ON DELETE RESTRICT ON UPDATE CASCADE;
-- Ejemplo: mysql -u user -p platform < src/main/resources/db/card_catalog.sql && mysql -u user -p platform < src/main/resources/db/user_cards.sql
CREATE TABLE IF NOT EXISTS platform.user_cards (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_code VARCHAR(32) NOT NULL,
    obtained_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_card (user_id, card_code),
    INDEX idx_user_cards_user (user_id),
    CONSTRAINT fk_user_cards_catalog FOREIGN KEY (card_code) REFERENCES platform.card_catalog (code) ON DELETE RESTRICT ON UPDATE CASCADE
);
