package com.register.wowlibre.domain.port.in.benefit_guild;

import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface BenefitGuildPort {
    void create(Long serverId, Long guildId, String guildName, Long[] benefits, boolean status, String transactionId);

    List<BenefitGuildModel> benefits(Long serverId, Long guildId, String transactionId);
}
