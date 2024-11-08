package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

@Data
public class GuildResponse {
    private Long id;
    private String name;
    @JsonProperty("leader_name")
    private String leaderName;
    @JsonProperty("emblem_style")
    private Long emblemStyle;
    @JsonProperty("emblem_color")
    private Long emblemColor;
    @JsonProperty("border_style")
    private Long borderStyle;
    @JsonProperty("border_color")
    private Long borderColor;
    private String info;
    private String motd;
    @JsonProperty("create_date")
    private Date createDate;
    @JsonProperty("bank_money")
    private Long bankMoney;
    private Long members;
    @JsonProperty("public_access")
    private boolean publicAccess;
    private String discord;
    public Cta cta;
    public String formattedBankMoney;
}
