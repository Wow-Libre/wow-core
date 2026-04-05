package com.register.wowlibre.domain.dto.character_migration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON almacenado en {@code raw_data} (dump del addon). Misma forma que {@code CharacterMigrationDumpDto}
 * en wow-libre-client / {@code CharacterMigrationDumpSectionsDto} en wow-libre-cms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterMigrationDumpDto {

    private MigrationDumpGinfDto ginf;
    private MigrationDumpUinfDto uinf;
    private JsonNode inventory;
    private JsonNode bags;
    private JsonNode bank;
    private JsonNode equipment;
    private JsonNode skills;
    private JsonNode spells;
    private JsonNode talents;
    private JsonNode glyphs;
    private JsonNode currency;
    private JsonNode quests;
    private JsonNode achievements;
    private JsonNode reputations;
    private JsonNode pets;
    private JsonNode mounts;
}
