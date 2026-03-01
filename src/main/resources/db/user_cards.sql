-- Cartas por usuario: cantidad de copias por (user_id, card_code). Permite duplicados y envío entre usuarios.
-- quantity: copias que tiene el usuario (al comprar sobre repetida se incrementa; al enviar se decrementa).
-- Si user_cards ya existía sin quantity: ALTER TABLE platform.user_cards ADD COLUMN quantity INT NOT NULL DEFAULT 1 AFTER card_code;
-- Ejecutar DESPUÉS de card_catalog.sql (hay FK card_code -> card_catalog.code).
CREATE TABLE IF NOT EXISTS platform.user_cards (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_code VARCHAR(32) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    obtained_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_card (user_id, card_code),
    INDEX idx_user_cards_user (user_id),
    CONSTRAINT fk_user_cards_catalog FOREIGN KEY (card_code) REFERENCES platform.card_catalog (code) ON DELETE RESTRICT ON UPDATE CASCADE
);
