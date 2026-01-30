package com.register.wowlibre.application.services.benefit_guild;

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
    public void create(Long realmId, Long guildId, String guildName, Long[] benefits, boolean status,
                       String transactionId) {
        BenefitGuildEntity associatedBenefit = new BenefitGuildEntity();

    }

    @Override
    public List<BenefitGuildEntity> benefits(Long realmId, Long guildId, String transactionId) {
        return obtainBenefitGuild.findByServerIdAndGuildIdAndStatusIsTrue(realmId, guildId, transactionId);
    }

    @Override
    public List<BenefitGuildEntity> findRemainingBenefitsForGuildAndServerIdAndCharacter(Long realmId, Long guildId,
                                                                                         Long characterId,
                                                                                         Long accountId,
                                                                                         String transactionId) {
        return obtainBenefitGuild.findRemainingBenefitsForGuildAndServerIdAndCharacter(realmId, guildId, characterId
                , accountId, transactionId);
    }


}
