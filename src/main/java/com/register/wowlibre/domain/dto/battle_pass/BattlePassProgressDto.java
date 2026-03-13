package com.register.wowlibre.domain.dto.battle_pass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattlePassProgressDto {

    @JsonProperty("character_level")
    private Integer characterLevel;
    @JsonProperty("claimed_reward_ids")
    private List<Long> claimedRewardIds;
}
