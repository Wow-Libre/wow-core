package com.register.wowlibre.domain.dto.account_game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkRealmPreviewResponse {
    @JsonProperty("realm_id")
    private Long realmId;
    @JsonProperty("realm_name")
    private String realmName;
    /**
     * Cuentas con personajes en el reino destino que aún no están vinculadas en la web a ese reino.
     */
    @Builder.Default
    @JsonProperty("linkable_accounts")
    private List<LinkRealmPreviewAccountDto> linkableAccounts = new ArrayList<>();
}
