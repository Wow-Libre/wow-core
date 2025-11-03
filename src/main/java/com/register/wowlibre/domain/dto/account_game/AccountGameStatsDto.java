package com.register.wowlibre.domain.dto.account_game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountGameStatsDto {
    private Long totalAccounts;
    private Long totalRealms;
}

