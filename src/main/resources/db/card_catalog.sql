-- Catálogo de cartas: código, URL, nombre y probabilidad de salir (1-100) al comprar un sobre.
-- Ejecutar PRIMERO (user_cards depende de esta tabla por FK).
-- Ejemplo: mysql -u user -p platform < src/main/resources/db/card_catalog.sql
CREATE TABLE IF NOT EXISTS platform.card_catalog (
    code VARCHAR(32) NOT NULL PRIMARY KEY,
    image_url VARCHAR(512) NOT NULL,
    display_name VARCHAR(128) DEFAULT NULL,
    probability TINYINT NOT NULL DEFAULT 50 COMMENT 'Probabilidad de salir en un sobre (1-100)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Si la tabla ya existía sin probability: ALTER TABLE platform.card_catalog ADD COLUMN probability TINYINT NOT NULL DEFAULT 50 COMMENT 'Probabilidad 1-100' AFTER display_name;

-- Ejemplos (sustituir URLs por las definitivas; probability 1-100)
INSERT INTO platform.card_catalog (code, image_url, display_name, probability) VALUES
    ('CARD_001', 'https://via.placeholder.com/300x420?text=CARD_001', 'Arthas ex', 30),
    ('CARD_002', 'https://via.placeholder.com/300x420?text=CARD_002', 'Carta 2', 70)
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url), display_name = VALUES(display_name), probability = VALUES(probability);
