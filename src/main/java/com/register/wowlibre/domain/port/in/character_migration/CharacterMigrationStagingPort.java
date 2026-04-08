package com.register.wowlibre.domain.port.in.character_migration;

import com.register.wowlibre.domain.dto.character_migration.*;
import com.register.wowlibre.domain.enums.*;

import java.util.*;

public interface CharacterMigrationStagingPort {

    /**
     * Orígenes de migración activos para mostrar en el cliente (realmlist del dump).
     */
    List<CharacterMigrationAllowedSourceOptionDto> listActiveAllowedSources(String tx);

    /**
     * Subida de dump (ruta {@code /me/upload}). {@code adminUserId} es el usuario autenticado dueño de la solicitud.
     *
     * @param allowedSourceId           obligatorio si hay al menos un origen activo en {@code
     *                                  character_migration_allowed_source};
     *                                  debe coincidir con {@code ginf.realmlist} del dump (sin distinguir mayúsculas).
     * @param targetAccountMode           {@link CharacterMigrationTargetAccountMode#CREATE_NEW} to create a game
     *                                    account with {@code targetGameAccountUsername}; {@link
     *                                    CharacterMigrationTargetAccountMode#USE_EXISTING} to attach to an account the
     *                                    user already has on this realm ({@code targetExistingAccountId} = emulator
     *                                    {@code account_id}).
     * @param targetGameAccountUsername   required when {@code targetAccountMode} is {@code CREATE_NEW} (5–20 chars).
     * @param targetExistingAccountId     required when {@code targetAccountMode} is {@code USE_EXISTING}.
     */
    CharacterMigrationStagingDetailDto uploadFromFile(
            Long userId, Long realmId, Long allowedSourceId, byte[] fileBytes,
            CharacterMigrationTargetAccountMode targetAccountMode,
            String targetGameAccountUsername,
            Long targetExistingAccountId,
            String tx);

    List<CharacterMigrationStagingListDto> listByRealm(Long realmId, String tx);

    /**
     * Listados del usuario autenticado. Si {@code realmId} es null, todas sus solicitudes (todos los reinos).
     */
    List<CharacterMigrationStagingListDto> listForUser(Long userId, Long realmId, String tx);

    CharacterMigrationStagingDetailDto getDetail(Long id, Long realmId, String tx);

    CharacterMigrationStagingDetailDto updateStatus(Long id, Long realmId, CharacterMigrationStagingStatus status,
                                                    String tx);
}
