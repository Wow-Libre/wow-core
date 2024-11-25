package com.register.wowlibre.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
public class PromotionDto {
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
    private Double amount;

    public PromotionDto(PromotionModel promotionModel) {
        this.id = promotionModel.getId();
        this.img = promotionModel.getImg();
        this.name = promotionModel.getName();
        this.description = promotionModel.getDescription();
        this.btnTxt = promotionModel.getBtnTxt();
        this.sendItem = promotionModel.getSendItem();
        this.type = promotionModel.getType();
        this.minLvl = promotionModel.getMinLvl();
        this.maxLvl = promotionModel.getMaxLvl();
        this.amount = promotionModel.getAmount();
    }
}
