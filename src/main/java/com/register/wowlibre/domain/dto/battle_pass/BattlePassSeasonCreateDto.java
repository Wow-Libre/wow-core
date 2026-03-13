package com.register.wowlibre.domain.dto.battle_pass;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattlePassSeasonCreateDto {

    @NotNull
    @JsonProperty("realm_id")
    private Long realmId;
    @NotNull
    private String name;
    @NotNull
    @JsonProperty("start_date")
    private String startDate;
    @NotNull
    @JsonProperty("end_date")
    private String endDate;
}
