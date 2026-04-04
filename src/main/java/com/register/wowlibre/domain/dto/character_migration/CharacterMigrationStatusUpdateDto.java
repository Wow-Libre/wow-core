package com.register.wowlibre.domain.dto.character_migration;

import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterMigrationStatusUpdateDto {

    @NotNull
    private CharacterMigrationStagingStatus status;
}
