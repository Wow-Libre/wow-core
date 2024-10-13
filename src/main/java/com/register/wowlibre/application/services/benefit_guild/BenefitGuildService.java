package com.register.wowlibre.application.services.benefit_guild;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.benefit_guild.*;
import com.register.wowlibre.domain.port.out.benefit_guild.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class BenefitGuildService implements BenefitGuildPort {
    private final ObtainBenefitGuild obtainBenefitGuild;
    private final SaveBenefitGuild saveBenefitGuild;

    public BenefitGuildService(ObtainBenefitGuild obtainBenefitGuild, SaveBenefitGuild saveBenefitGuild) {
        this.obtainBenefitGuild = obtainBenefitGuild;
        this.saveBenefitGuild = saveBenefitGuild;
    }


    @Override
    public void create(Long serverId, Long guildId, String guildName, Long[] benefits, boolean status,
                       String transactionId) {
        BenefitGuildEntity associatedBenefit = new BenefitGuildEntity();

    }

    @Override
    public List<BenefitGuildModel> benefits(Long serverId, Long guildId, String transactionId) {
        return obtainBenefitGuild.findByServerIdAndGuildIdAndStatusIsTrue(serverId, guildId, transactionId).stream().map(this::mapToModel).toList();
    }

    private BenefitGuildModel mapToModel(BenefitGuildEntity benefitGuild) {
        return new BenefitGuildModel(benefitGuild.getId(), benefitGuild.getBenefitId(),
                benefitGuild.getStatus());
    }
}
