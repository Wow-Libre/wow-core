package com.register.wowlibre.domain.dto.character_migration;

import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CharacterMigrationStagingListDto {
    private Long id;
    /** Reino al que pertenece el staging (útil para el cliente en /accounts). */
    private Long realmId;
    private String characterName;
    private String characterGuid;
    private CharacterMigrationStagingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
