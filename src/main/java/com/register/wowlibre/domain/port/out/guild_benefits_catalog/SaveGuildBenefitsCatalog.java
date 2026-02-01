package com.register.wowlibre.domain.port.out.guild_benefits_catalog;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveGuildBenefitsCatalog {
    void save(GuildBenefitCatalogEntity guildBenefitCatalogEntity);
}
