package com.register.wowlibre.domain.dto.character_migration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bloque {@code ginf} del dump; mismo contrato que {@code MigrationDumpGinfDto} en wow-libre-client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrationDumpGinfDto {

    private String realmlist;
    private String realm;
    private String realmName;
}
