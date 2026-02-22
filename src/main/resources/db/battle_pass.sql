-- Battle pass: temporadas por reino, premios por nivel (1-80), reclamaciones por personaje/cuenta/reino
CREATE TABLE IF NOT EXISTS platform.battle_pass_season (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    realm_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_realm_dates (realm_id, start_date, end_date)
);

CREATE TABLE IF NOT EXISTS platform.battle_pass_reward (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    level INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(512),
    core_item_id INT NOT NULL,
    wowhead_id INT NULL,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_season_level (season_id, level),
    INDEX idx_season (season_id),
    CONSTRAINT fk_bp_reward_season FOREIGN KEY (season_id) REFERENCES platform.battle_pass_season(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS platform.battle_pass_claim (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    realm_id INT NOT NULL,
    account_id INT NOT NULL,
    character_id INT NOT NULL,
    reward_id BIGINT NOT NULL,
    claimed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_character_reward (season_id, realm_id, account_id, character_id, reward_id),
    INDEX idx_character_progress (season_id, realm_id, account_id, character_id),
    CONSTRAINT fk_bp_claim_season FOREIGN KEY (season_id) REFERENCES platform.battle_pass_season(id),
    CONSTRAINT fk_bp_claim_reward FOREIGN KEY (reward_id) REFERENCES platform.battle_pass_reward(id)
);
