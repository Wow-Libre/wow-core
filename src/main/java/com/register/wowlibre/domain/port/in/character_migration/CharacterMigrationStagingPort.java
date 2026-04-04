package com.register.wowlibre.domain.port.in.character_migration;

import com.register.wowlibre.domain.dto.character_migration.CharacterMigrationStagingDetailDto;
import com.register.wowlibre.domain.dto.character_migration.CharacterMigrationStagingListDto;
import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;

import java.util.List;

public interface CharacterMigrationStagingPort {

    CharacterMigrationStagingDetailDto uploadFromFile(Long adminUserId, Long realmId, byte[] fileBytes, String tx);

    List<CharacterMigrationStagingListDto> listByRealm(Long realmId, String tx);

    /**
     * Listados del usuario autenticado. Si {@code realmId} es null, todas sus solicitudes (todos los reinos).
     */
    List<CharacterMigrationStagingListDto> listForUser(Long userId, Long realmId, String tx);

    CharacterMigrationStagingDetailDto getDetail(Long id, Long realmId, String tx);

    CharacterMigrationStagingDetailDto updateStatus(Long id, Long realmId, CharacterMigrationStagingStatus status, String tx);
}
