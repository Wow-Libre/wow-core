package com.register.wowlibre.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

@Data
public class PromotionModel {
    private Long id;
    private String img;
    private String name;
    private String description;
    @JsonProperty("btn_txt")
    private String btnTxt;
    @JsonProperty("send_item")
    private Boolean sendItem;
    private String type;
    @JsonProperty("min_lvl")
    private Integer minLvl;
    @JsonProperty("max_lvl")
    private Integer maxLvl;
    private List<Items> items;
    private Double amount;
    @JsonProperty("server_id")
    private Long serverId;

    @Data
    public static class Items {
        public String code;
        public Integer quantity;
    }
}
