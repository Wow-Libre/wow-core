package com.register.wowlibre.service;

import com.register.wowlibre.application.services.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.out.banners.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BannersServiceTest {

    private ObtainBanners obtainBanners;
    private SaveBanners saveBanners;
    private DeleteBanners deleteBanners;
    private BannersService service;

    @BeforeEach
    void setUp() {
        obtainBanners = mock(ObtainBanners.class);
        saveBanners = mock(SaveBanners.class);
        deleteBanners = mock(DeleteBanners.class);
        service = new BannersService(obtainBanners, saveBanners, deleteBanners);
    }

    @Test
    void findByLanguage_returnsMappedList() {
        BannersEntity entity = new BannersEntity();
        entity.setId(1L);
        entity.setMediaUrl("url");
        entity.setAlt("alt");
        entity.setLanguage("es");
        entity.setType(BannerType.IMAGE);
        entity.setLabel("label");
        when(obtainBanners.findByLanguage("es")).thenReturn(List.of(entity));

        List<BannerModel> result = service.findByLanguage("es");

        assertEquals(1, result.size());
        assertEquals("url", result.get(0).mediaUrl());
        assertEquals("IMAGE", result.get(0).type());
    }

    @Test
    void saveBanner_validBanner_savesEntity() {
        BannerDto dto = new BannerDto();
        dto.setType("IMAGE");
        dto.setLanguage("es");
        dto.setMediaUrl("url");
        dto.setAlt("alt");
        dto.setLabel("label");

        when(obtainBanners.isValidBanner("es", BannerType.IMAGE, "tx")).thenReturn(true);

        service.saveBanner(dto, "tx");

        verify(saveBanners).save(any(BannersEntity.class));
    }

    @Test
    void saveBanner_invalidType_throwsException() {
        BannerDto dto = new BannerDto();
        dto.setType("INVALID");
        dto.setLanguage("es");

        Exception ex = assertThrows(InternalException.class, () -> service.saveBanner(dto, "tx"));
        assertTrue(ex.getMessage().contains("Invalid banner type"));
        verify(saveBanners, never()).save(any());
    }

    @Test
    void saveBanner_notValidBanner_throwsException() {
        BannerDto dto = new BannerDto();
        dto.setType("IMAGE");
        dto.setLanguage("es");

        when(obtainBanners.isValidBanner("es", BannerType.IMAGE, "tx")).thenReturn(false);

        Exception ex = assertThrows(InternalException.class, () -> service.saveBanner(dto, "tx"));
        assertTrue(ex.getMessage().contains("You cannot add a banner"));
        verify(saveBanners, never()).save(any());
    }

    @Test
    void deleteBanner_found_deletesEntity() {
        BannersEntity entity = new BannersEntity();
        when(obtainBanners.findById(1L)).thenReturn(Optional.of(entity));

        service.deleteBanner(1L, "tx");

        verify(deleteBanners).delete(entity);
    }

    @Test
    void deleteBanner_notFound_doesNothing() {
        when(obtainBanners.findById(1L)).thenReturn(Optional.empty());

        service.deleteBanner(1L, "tx");

        verify(deleteBanners, never()).delete(any());
    }
}
