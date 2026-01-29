package com.register.wowlibre.domain.port.in.guild_benefits_catalog;

import com.register.wowlibre.domain.dto.guild_benefits_catalog.*;

import java.util.*;

public interface GuildBenefitsCatalogPort {
    void create(CreateGuildBenefitsCatalog createGuildBenefitsCatalog, String transactionId);

    void update(Long benefitId, UpdateGuildBenefitsCatalog updateGuildBenefitsCatalog, String transactionId);

    void delete(Long benefitId, String transactionId);

    GuildBenefitsCatalogDto getBenefitById(Long benefitId, String transactionId);

    List<GuildBenefitsCatalogDto> getAllBenefits(String language, String transactionId);
}
