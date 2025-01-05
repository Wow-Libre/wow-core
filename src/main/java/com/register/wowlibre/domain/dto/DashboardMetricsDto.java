package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;

@AllArgsConstructor
@Getter
public class DashboardMetricsDto {
    private Long totalUsers;
    private Long onlineUsers;
    private Long totalGuilds;
    private Long externalRegistrations;
    private Long characterCount;
    private Long hordas;
    private Long alianzas;
    private Long redeemedPromotions;
    private List<LevelRangeModel> rangeLevel;

}
