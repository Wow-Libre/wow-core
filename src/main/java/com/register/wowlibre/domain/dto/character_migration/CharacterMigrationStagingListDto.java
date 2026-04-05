package com.register.wowlibre.domain.dto.character_migration;

import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CharacterMigrationStagingListDto {
    private Long id;
    /**
     * Reino al que pertenece el staging (útil para el cliente en /accounts).
     * En JSON: {@code realm_id} (Jackson {@code SNAKE_CASE} global).
     */
    private Long realmId;
    /**
     * Columna {@code character_name} en {@code platform.character_migration_staging}.
     * En JSON: {@code character_name}.
     */
    private String characterName;
    /** En JSON: {@code character_guid}. */
    private String characterGuid;
    /** En JSON: {@code target_game_account_username}. */
    private String targetGameAccountUsername;
    private CharacterMigrationStagingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
