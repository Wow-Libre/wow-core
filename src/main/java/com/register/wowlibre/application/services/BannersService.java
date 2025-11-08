package com.register.wowlibre.application.services;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.banners.*;
import com.register.wowlibre.domain.port.out.banners.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class BannersService implements BannersPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(BannersService.class);

    private final ObtainBanners obtainBanners;
    private final SaveBanners saveBanners;
    private final DeleteBanners deleteBanners;

    public BannersService(ObtainBanners obtainBanners, SaveBanners saveBanners, DeleteBanners deleteBanners) {
        this.obtainBanners = obtainBanners;
        this.saveBanners = saveBanners;
        this.deleteBanners = deleteBanners;
    }

    @Override
    public List<BannerModel> findByLanguage(String language) {
        return obtainBanners.findByLanguage(language).stream().map(this::mapToModel).toList();
    }

    @Override
    public void saveBanner(BannerDto bannerModel, String transactionId) {
        BannerType bannersType = BannerType.fromName(bannerModel.getType());
        final String language = bannerModel.getLanguage();

        if (bannersType == null) {
            LOGGER.error("[BannersService] [saveBanner] Invalid banner type - type: {}, language: {}, transactionId: {}",
                    bannerModel.getType(), language, transactionId);
            throw new InternalException("Invalid banner type: " + bannerModel.getType(), transactionId);
        }

        if (!obtainBanners.isValidBanner(language, bannersType, transactionId)) {
            LOGGER.error("[BannersService] [saveBanner] Banner validation failed - type: {}, language: {}, " +
                    "mediaUrl: {}, transactionId: {}", bannersType.name(), language, bannerModel.getMediaUrl(), transactionId);
            throw new InternalException("You cannot add a banner of two different types, either an image or a video," +
                    " and the maximum is 5 resources.", transactionId);
        }

        BannersEntity bannersEntity = new BannersEntity();
        bannersEntity.setMediaUrl(bannerModel.getMediaUrl());
        bannersEntity.setAlt(bannerModel.getAlt());
        bannersEntity.setLanguage(bannerModel.getLanguage());
        bannersEntity.setType(bannersType);
        bannersEntity.setLabel(bannerModel.getLabel());

        saveBanners.save(bannersEntity);
    }

    @Override
    public void deleteBanner(Long bannerId, String transactionId) {
        obtainBanners.findById(bannerId)
                .ifPresent(deleteBanners::delete);
    }

    private BannerModel mapToModel(BannersEntity entity) {
        return new BannerModel(entity.getId(),
                entity.getMediaUrl(),
                entity.getAlt(),
                entity.getLanguage(),
                entity.getType().name(),
                entity.getLabel());
    }
}
