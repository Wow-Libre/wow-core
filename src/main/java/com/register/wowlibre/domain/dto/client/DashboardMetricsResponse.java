package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;

@Data
public class DashboardMetricsResponse {
    @JsonProperty("total_users")
    private Long totalUsers;
    @JsonProperty("online_users")
    private Long onlineUsers;
    @JsonProperty("total_guilds")
    private Long totalGuilds;
    @JsonProperty("external_registrations")
    private Long externalRegistrations;
    @JsonProperty("character_count")
    private Long characterCount;
    private Long hordas;
    private Long alianzas;
    @JsonProperty("range_level")
    private List<LevelRangeModel> rangeLevel;

}
