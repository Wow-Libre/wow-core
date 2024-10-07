package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
public class CharacterDetailDto {
    private Long id;
    private String name;
    @JsonProperty("race_id")
    private Integer raceId;
    @JsonProperty("race_logo")
    private String raceLogo;
    private String race;
    @JsonProperty("class")
    private String classCharacters;
    @JsonProperty("class_id")
    private Integer classId;
    @JsonProperty("class_logo")
    private String classLogo;
    private Integer gender;
    private Integer level;
    private Integer xp;
    private Long money;
    @JsonProperty("logout_time")
    private Integer logoutTime;
    @JsonProperty("total_time")
    private Integer totalTime;
}
