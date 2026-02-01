package com.register.wowlibre.infrastructure.repositories.guild_benefits_catalog;

import com.register.wowlibre.domain.port.out.guild_benefits_catalog.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaGuildBenefitsCatalogAdapter implements ObtainGuildBenefitsCatalog, SaveGuildBenefitsCatalog,
        DeleteGuildBenefitsCatalog {
    private final GuildBenefitsCatalogRepository guildBenefitsCatalogRepository;

    public JpaGuildBenefitsCatalogAdapter(GuildBenefitsCatalogRepository guildBenefitsCatalogRepository) {
        this.guildBenefitsCatalogRepository = guildBenefitsCatalogRepository;
    }

    @Override
    public Optional<GuildBenefitCatalogEntity> findById(Long id) {
        return guildBenefitsCatalogRepository.findById(id);
    }

    @Override
    public List<GuildBenefitCatalogEntity> findAllByLanguage(String language) {
        return guildBenefitsCatalogRepository.findAllByLanguage(language);
    }

    @Override
    public void save(GuildBenefitCatalogEntity guildBenefitCatalogEntity) {
        guildBenefitsCatalogRepository.save(guildBenefitCatalogEntity);
    }

    @Override
    public void delete(GuildBenefitCatalogEntity guildBenefitCatalogEntity) {
        guildBenefitsCatalogRepository.delete(guildBenefitCatalogEntity);
    }
}
