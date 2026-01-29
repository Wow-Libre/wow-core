package com.register.wowlibre.domain.port.out.guild_benefits_catalog;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainGuildBenefitsCatalog {
    Optional<GuildBenefitCatalogEntity> findById(Long id);

    List<GuildBenefitCatalogEntity> findAllByLanguage(String language);
}
