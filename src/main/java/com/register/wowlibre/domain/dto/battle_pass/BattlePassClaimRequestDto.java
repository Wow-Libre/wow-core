package com.register.wowlibre.domain.dto.battle_pass;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattlePassClaimRequestDto {

    @NotNull
    @JsonProperty("realm_id")
    private Long realmId;
    @NotNull
    @JsonProperty("account_id")
    private Long accountId;
    @NotNull
    @JsonProperty("character_id")
    private Long characterId;
    @NotNull
    @JsonProperty("season_id")
    private Long seasonId;
    @NotNull
    @JsonProperty("reward_id")
    private Long rewardId;
}
