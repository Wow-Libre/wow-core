package com.register.wowlibre.domain.dto.account_game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Una cuenta de juego (mismo account_id en el integrador) que puede vincularse al reino destino
 * si el usuario ya tiene una fila account_game en otro reino ({@code sourceAccountGameId}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkRealmPreviewAccountDto {
    @JsonProperty("account_id")
    private Long accountId;
    /**
     * ID de la fila {@code account_game} en otro reino (se envía en POST /link como source_account_game_id).
     */
    @JsonProperty("source_account_game_id")
    private Long sourceAccountGameId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("has_characters")
    private boolean hasCharacters;
    @JsonProperty("character_count")
    private int characterCount;
    @JsonProperty("already_linked")
    private boolean alreadyLinked;
    @JsonProperty("can_link")
    private boolean canLink;
}
