package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.io.*;

@Data
public class CharacterDetailDto  implements Serializable {
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
    private Double money;
    @JsonProperty("logout_time")
    private Integer logoutTime;
    @JsonProperty("total_time")
    private Integer totalTime;
    private Integer dream;
    private Integer hunger;
    private Integer thirst;
}
