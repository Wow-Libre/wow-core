package com.register.wowlibre.domain.dto.battle_pass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattlePassRewardDto {

    private Long id;
    @JsonProperty("season_id")
    private Long seasonId;
    private Integer level;
    private String name;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("core_item_id")
    private Integer coreItemId;
    @JsonProperty("wowhead_id")
    private Integer wowheadId;
}
