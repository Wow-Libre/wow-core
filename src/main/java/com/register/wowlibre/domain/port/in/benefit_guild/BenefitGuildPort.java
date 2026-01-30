package com.register.wowlibre.domain.port.in.benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface BenefitGuildPort {
    void create(Long serverId, Long guildId, String guildName, Long[] benefits, boolean status, String transactionId);

    List<BenefitGuildEntity> benefits(Long serverId, Long guildId, String transactionId);

    List<BenefitGuildEntity> findRemainingBenefitsForGuildAndServerIdAndCharacter(Long serverId, Long guildId,
                                                                                  Long characterId, Long accountId,
                                                                                  String transactionId);

}
