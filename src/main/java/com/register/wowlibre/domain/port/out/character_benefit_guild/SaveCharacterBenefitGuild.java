package com.register.wowlibre.domain.port.out.character_benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveCharacterBenefitGuild {
    void save(CharacterBenefitGuildEntity characterBenefitGuild, String transactionId);
}
