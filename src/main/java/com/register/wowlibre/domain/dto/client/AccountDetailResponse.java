package com.register.wowlibre.domain.dto.client;


import java.time.*;

public record AccountDetailResponse(
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
        AccountBannedResponse accountBanned
) {



}
