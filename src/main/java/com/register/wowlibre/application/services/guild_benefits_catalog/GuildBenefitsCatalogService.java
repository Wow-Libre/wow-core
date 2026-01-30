package com.register.wowlibre.application.services.guild_benefits_catalog;

import com.register.wowlibre.domain.dto.guild_benefits_catalog.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.guild_benefits_catalog.*;
import com.register.wowlibre.domain.port.out.guild_benefits_catalog.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class GuildBenefitsCatalogService implements GuildBenefitsCatalogPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildBenefitsCatalogService.class);

    private final ObtainGuildBenefitsCatalog obtainGuildBenefitsCatalogPort;
    private final SaveGuildBenefitsCatalog saveGuildBenefitsCatalogPort;
    private final DeleteGuildBenefitsCatalog deleteGuildBenefitsCatalogPort;

    public GuildBenefitsCatalogService(ObtainGuildBenefitsCatalog obtainGuildBenefitsCatalogPort,
                                       SaveGuildBenefitsCatalog saveGuildBenefitsCatalogPort,
                                       DeleteGuildBenefitsCatalog deleteGuildBenefitsCatalogPort) {
        this.obtainGuildBenefitsCatalogPort = obtainGuildBenefitsCatalogPort;
        this.saveGuildBenefitsCatalogPort = saveGuildBenefitsCatalogPort;
        this.deleteGuildBenefitsCatalogPort = deleteGuildBenefitsCatalogPort;
    }

    @Override
    public void create(CreateGuildBenefitsCatalog createGuildBenefitsCatalog, String transactionId) {
        GuildBenefitCatalogEntity guildBenefitCatalogEntity = new GuildBenefitCatalogEntity();
        guildBenefitCatalogEntity.setTitle(createGuildBenefitsCatalog.getTitle());
        guildBenefitCatalogEntity.setSubTitle(createGuildBenefitsCatalog.getSubTitle());
        guildBenefitCatalogEntity.setDescription(createGuildBenefitsCatalog.getDescription());
        guildBenefitCatalogEntity.setImageUrl(createGuildBenefitsCatalog.getImageUrl());
        guildBenefitCatalogEntity.setCoreCode(createGuildBenefitsCatalog.getCoreCode());
        guildBenefitCatalogEntity.setQuantity(createGuildBenefitsCatalog.getQuantity());
        guildBenefitCatalogEntity.setActive(true);
        guildBenefitCatalogEntity.setExternalUrl(createGuildBenefitsCatalog.getExternalUrl());
        saveGuildBenefitsCatalogPort.save(guildBenefitCatalogEntity);
    }

    @Override
    public void update(UpdateGuildBenefitsCatalog updateGuildBenefitsCatalog, String transactionId) {
        Optional<GuildBenefitCatalogEntity> guildBenefitCatalog =
                obtainGuildBenefitsCatalogPort.findById(updateGuildBenefitsCatalog.getBenefitId());

        if (guildBenefitCatalog.isEmpty()) {
            throw new InternalException("Benefit Catalog Not Found", transactionId);
        }
        GuildBenefitCatalogEntity entityToUpdate = guildBenefitCatalog.get();
        entityToUpdate.setTitle(updateGuildBenefitsCatalog.getTitle());
        entityToUpdate.setSubTitle(updateGuildBenefitsCatalog.getSubTitle());
        entityToUpdate.setDescription(updateGuildBenefitsCatalog.getDescription());
        entityToUpdate.setImageUrl(updateGuildBenefitsCatalog.getImageUrl());
        entityToUpdate.setCoreCode(updateGuildBenefitsCatalog.getCoreCode());
        entityToUpdate.setQuantity(updateGuildBenefitsCatalog.getQuantity());
        entityToUpdate.setExternalUrl(updateGuildBenefitsCatalog.getExternalUrl());
        saveGuildBenefitsCatalogPort.save(entityToUpdate);
    }

    @Override
    public void delete(Long benefitId, String transactionId) {
        Optional<GuildBenefitCatalogEntity> guildBenefitCatalog = obtainGuildBenefitsCatalogPort.findById(benefitId);

        if (guildBenefitCatalog.isEmpty()) {
            LOGGER.error("Guild Benefits Catalog Not Found [{}]", transactionId);
            throw new InternalException("This benefit requested to be removed does not exist or is already " +
                    "deactivated.", transactionId);
        }

        deleteGuildBenefitsCatalogPort.delete(guildBenefitCatalog.get());
    }

    @Override
    public GuildBenefitsCatalogDto getBenefitById(Long benefitId, String transactionId) {
        return obtainGuildBenefitsCatalogPort.findById(benefitId).map(this::mapEntityToDto).orElse(null);
    }

    @Override
    public List<GuildBenefitsCatalogDto> getAllBenefits(String language, String transactionId) {
        return obtainGuildBenefitsCatalogPort.findAllByLanguage(language).stream().map(this::mapEntityToDto).toList();
    }

    private GuildBenefitsCatalogDto mapEntityToDto(GuildBenefitCatalogEntity entity) {
        return GuildBenefitsCatalogDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .subTitle(entity.getSubTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .coreCode(entity.getCoreCode())
                .quantity(entity.getQuantity())
                .isActive(entity.isActive())
                .externalUrl(entity.getExternalUrl())
                .build();
    }
}
