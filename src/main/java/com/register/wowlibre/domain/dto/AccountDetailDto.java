package com.register.wowlibre.domain.dto;


import com.register.wowlibre.domain.dto.client.*;
import lombok.*;

import java.time.*;

@Builder
public record AccountDetailDto(
        Long id,
        String username,
        String email,
        String expansion,
        boolean online,
        String failedLogins,
        LocalDate joinDate,
        String lastIp,
        String muteReason,
        String muteBy,
        boolean mute,
        LocalDate lastLogin,
        String os,
        String server,
        AccountBannedResponse accountBanned
) {
}
