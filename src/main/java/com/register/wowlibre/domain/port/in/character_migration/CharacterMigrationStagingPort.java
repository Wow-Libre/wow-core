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
     * @param allowedSourceId obligatorio si hay al menos un origen activo en {@code
     * character_migration_allowed_source};
     *                        debe coincidir con {@code ginf.realmlist} del dump (sin distinguir mayúsculas).
     * @param targetGameAccountUsername nombre de usuario de la cuenta de juego en el reino destino (5–20 caracteres).
     */
    CharacterMigrationStagingDetailDto uploadFromFile(
            Long adminUserId, Long realmId, Long allowedSourceId, byte[] fileBytes,
            String targetGameAccountUsername, String tx);

    List<CharacterMigrationStagingListDto> listByRealm(Long realmId, String tx);

    /**
     * Listados del usuario autenticado. Si {@code realmId} es null, todas sus solicitudes (todos los reinos).
     */
    List<CharacterMigrationStagingListDto> listForUser(Long userId, Long realmId, String tx);

    CharacterMigrationStagingDetailDto getDetail(Long id, Long realmId, String tx);

    CharacterMigrationStagingDetailDto updateStatus(Long id, Long realmId, CharacterMigrationStagingStatus status,
                                                    String tx);
}
