package com.register.wowlibre.domain.dto.account_game;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LinkRealmRequestDto {
    @NotNull
    @JsonProperty("realm_id")
    private Long realmId;

    /**
     * Fila {@code account_game} de referencia (mismo usuario). Si hay un solo {@code account_id} activo, puede omitirse.
     */
    @JsonProperty("source_account_game_id")
    private Long sourceAccountGameId;
}
