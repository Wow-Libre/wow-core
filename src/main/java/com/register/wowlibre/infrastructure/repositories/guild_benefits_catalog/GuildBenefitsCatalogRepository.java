package com.register.wowlibre.infrastructure.repositories.guild_benefits_catalog;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface GuildBenefitsCatalogRepository extends CrudRepository<GuildBenefitCatalogEntity, Long> {
    @Query("SELECT g FROM GuildBenefitCatalogEntity g WHERE g.language = :language AND g.isActive = true")
    List<GuildBenefitCatalogEntity> findAllByLanguage(@Param("language") String language);
}
