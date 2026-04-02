package com.register.wowlibre.domain.dto.account_game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkRealmPreviewResponse {
    @JsonProperty("realm_id")
    private Long realmId;
    @JsonProperty("realm_name")
    private String realmName;
    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("has_characters")
    private boolean hasCharacters;
    @JsonProperty("character_count")
    private int characterCount;
    @JsonProperty("already_linked")
    private boolean alreadyLinked;
    /**
     * Se puede confirmar la vinculación (reino activo y aún no hay fila para esta cuenta en este reino).
     */
    @JsonProperty("can_link")
    private boolean canLink;
}
