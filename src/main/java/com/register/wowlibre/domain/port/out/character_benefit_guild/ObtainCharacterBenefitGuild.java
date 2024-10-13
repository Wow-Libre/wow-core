package com.register.wowlibre.domain.port.out.character_benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainCharacterBenefitGuild {
    List<CharacterBenefitGuildEntity> findByCharacterIdAndAccountId(Long characterId, Long accountId,
                                                                    String transactionId);

}
