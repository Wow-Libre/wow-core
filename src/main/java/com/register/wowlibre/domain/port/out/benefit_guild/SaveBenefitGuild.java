package com.register.wowlibre.domain.port.out.benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveBenefitGuild {
    void save(BenefitGuildEntity benefitGuild, String transactionId);
}
