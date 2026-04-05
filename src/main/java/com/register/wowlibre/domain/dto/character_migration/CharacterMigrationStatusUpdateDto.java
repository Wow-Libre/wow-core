package com.register.wowlibre.domain.dto.character_migration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterMigrationStatusUpdateDto {

    @NotNull
    @JsonProperty("status")
    private CharacterMigrationStagingStatus status;
}
