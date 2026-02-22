package com.register.wowlibre.domain.dto.battle_pass;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattlePassRewardCreateDto {

    @NotNull
    @JsonProperty("season_id")
    private Long seasonId;
    @NotNull
    private Integer level;
    @NotNull
    private String name;
    @JsonProperty("image_url")
    private String imageUrl;
    @NotNull
    @JsonProperty("core_item_id")
    private Integer coreItemId;
    @JsonProperty("wowhead_id")
    private Integer wowheadId;
}
