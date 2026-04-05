package com.register.wowlibre.domain.dto.character_migration;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

/**
 * Bloque {@code uinf} del dump; mismo contrato que {@code MigrationDumpUinfDto} en wow-libre-client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrationDumpUinfDto {

    private String account;
    private String character;
    private String name;
    private String race;
    @JsonProperty("class")
    private String clase;
    private Long honor;
    private Integer kills;
    private Integer level;
    private Long money;
    private Integer gender;
    @JsonProperty("arenapoints")
    private Integer arenaPoints;
}
