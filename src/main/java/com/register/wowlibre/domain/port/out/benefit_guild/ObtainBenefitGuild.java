package com.register.wowlibre.domain.port.out.benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainBenefitGuild {
    List<BenefitGuildEntity> findByServerIdAndGuildIdAndStatusIsTrue(Long serverId, Long guildId, String transactionId);
    List<BenefitGuildEntity> findRemainingBenefitsForGuildAndServerIdAndCharacter(Long serverId, Long guildId, Long characterId,
                                                                      Long accountId ,String transactionId);

}
