package com.register.wowlibre.infrastructure.repositories.character_benefit_guild;

import com.register.wowlibre.domain.port.out.character_benefit_guild.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaCharacterBenefitGuildAdapter implements ObtainCharacterBenefitGuild, SaveCharacterBenefitGuild {
    private final CharacterBenefitGuildRepository characterBenefitGuildRepository;

    public JpaCharacterBenefitGuildAdapter(CharacterBenefitGuildRepository characterBenefitGuildRepository) {
        this.characterBenefitGuildRepository = characterBenefitGuildRepository;
    }

    @Override
    public List<CharacterBenefitGuildEntity> findByCharacterIdAndAccountId(Long characterId, Long accountId,
                                                                           String transactionId) {
        return characterBenefitGuildRepository.findByCharacterIdAndAccountId(characterId, accountId);
    }

    @Override
    public void save(CharacterBenefitGuildEntity characterBenefitGuild, String transactionId) {
        characterBenefitGuildRepository.save(characterBenefitGuild);
    }
}
