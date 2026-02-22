package com.register.wowlibre.domain.dto.battle_pass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattlePassSeasonDto {

    private Long id;
    @JsonProperty("realm_id")
    private Long realmId;
    private String name;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("is_active")
    private Boolean isActive;
}
