package com.register.wowlibre.domain.dto.character_migration;

import lombok.*;

/**
 * Cuerpo JSON enviado a wow-libre-client al aprobar una migración ({@code COMPLETED}).
 * Alineado con {@code WowLibreClientCharacterMigrationApprovedDto} en wow-libre-client y el contrato TS en
 * wow-libre-cms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WowLibreClientMigrationApprovedPayloadDto {

    public static final int SCHEMA_VERSION = 1;

    private Integer schemaVersion;
    private Long migrationId;
    private Long realmId;
    private Long userId;
    private Long accountId;
    private String characterName;
    private String characterGuid;
    private String emulator;
    /**
     * Siempre {@code COMPLETED} en este flujo.
     */
    private String status;
    /**
     * ISO-8601 (ej. {@code Instant#toString()}).
     */
    private String approvedAt;
    /**
     * Contenido de {@code raw_data} en BD, deserializado al DTO estable del contrato.
     */
    @Builder.Default
    private CharacterMigrationDumpDto dump = CharacterMigrationDumpDto.builder().build();
}
