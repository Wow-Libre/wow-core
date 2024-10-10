package com.register.wowlibre.domain.dto;

import com.register.wowlibre.infrastructure.entities.*;


public record AccountVerificationDto(ServerEntity server, AccountGameEntity accountGame) {
}
