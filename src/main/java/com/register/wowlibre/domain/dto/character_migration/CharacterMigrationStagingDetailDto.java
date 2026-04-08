package com.register.wowlibre.domain.dto.character_migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import com.register.wowlibre.domain.enums.CharacterMigrationTargetAccountMode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CharacterMigrationStagingDetailDto {
    private Long id;
    private Long userId;
    private Long realmId;
    private String characterName;
    private String characterGuid;
    private CharacterMigrationTargetAccountMode targetAccountMode;
    /** Emulator game account id when {@code targetAccountMode} is {@code USE_EXISTING}. */
    private Long targetExistingAccountId;
    /** Game username for new accounts; for existing accounts, display copy from the linked account. */
    private String targetGameAccountUsername;
    private CharacterMigrationStagingStatus status;
    private String validationErrors;
    private JsonNode rawData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
