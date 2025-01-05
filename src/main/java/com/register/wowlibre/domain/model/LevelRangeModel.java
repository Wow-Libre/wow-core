package com.register.wowlibre.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelRangeModel {
    @JsonProperty("level_range")
    private String levelRange;
    @JsonProperty("user_count")
    private Long userCount;
}
