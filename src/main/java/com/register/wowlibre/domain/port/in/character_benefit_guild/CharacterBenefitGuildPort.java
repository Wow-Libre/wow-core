package com.register.wowlibre.domain.port.in.character_benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;

public interface CharacterBenefitGuildPort {
    void save(Long characterId, Long accountId, BenefitGuildEntity benefitGuildId, boolean send, String transactionId);
}
