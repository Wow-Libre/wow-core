package com.register.wowlibre.domain.dto.character_migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
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
    private CharacterMigrationStagingStatus status;
    private String validationErrors;
    private JsonNode rawData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
