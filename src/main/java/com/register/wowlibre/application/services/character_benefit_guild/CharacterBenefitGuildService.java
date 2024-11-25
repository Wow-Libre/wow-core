package com.register.wowlibre.application.services.character_benefit_guild;

import com.register.wowlibre.domain.port.in.character_benefit_guild.*;
import com.register.wowlibre.domain.port.out.character_benefit_guild.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

@Service
public class CharacterBenefitGuildService implements CharacterBenefitGuildPort {
    private final SaveCharacterBenefitGuild saveCharacterBenefitGuild;

    public CharacterBenefitGuildService(SaveCharacterBenefitGuild saveCharacterBenefitGuild) {
        this.saveCharacterBenefitGuild = saveCharacterBenefitGuild;
    }


    @Override
    public void save(Long characterId, Long accountId, BenefitGuildEntity benefitGuildId, boolean send,
                     String transactionId) {
        CharacterBenefitGuildEntity characterBenefitGuild = new CharacterBenefitGuildEntity();
        characterBenefitGuild.setBenefitSend(send);
        characterBenefitGuild.setCharacterId(characterId);
        characterBenefitGuild.setAccountId(accountId);
        characterBenefitGuild.setBenefitGuildId(benefitGuildId);
        saveCharacterBenefitGuild.save(characterBenefitGuild, transactionId);
    }
}
