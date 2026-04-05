package com.register.wowlibre.domain.dto.character_migration;

import lombok.Builder;
import lombok.Data;

/**
 * Opción pública para el selector de origen de migración (GET /me/allowed-sources).
 */
@Data
@Builder
public class CharacterMigrationAllowedSourceOptionDto {
    private Long id;
    private String realmlistHost;
    private String displayName;
}
