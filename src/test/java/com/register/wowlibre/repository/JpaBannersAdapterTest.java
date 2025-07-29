package com.register.wowlibre.repository;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.banners.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaBannersAdapterTest {

    private BannersRepository bannersRepository;
    private JpaBannersAdapter jpaBannersAdapter;

    @BeforeEach
    void setUp() {
        bannersRepository = mock(BannersRepository.class);
        jpaBannersAdapter = new JpaBannersAdapter(bannersRepository);
    }

    @Test
    void testFindByLanguage() {
        List<BannersEntity> banners = List.of(new BannersEntity());
        when(bannersRepository.findByLanguage("en")).thenReturn(banners);

        List<BannersEntity> result = jpaBannersAdapter.findByLanguage("en");

        assertEquals(1, result.size());
        verify(bannersRepository).findByLanguage("en");
    }

    @Test
    void testFindById() {
        BannersEntity banner = new BannersEntity();
        when(bannersRepository.findById(1L)).thenReturn(Optional.of(banner));

        Optional<BannersEntity> result = jpaBannersAdapter.findById(1L);

        assertTrue(result.isPresent());
        verify(bannersRepository).findById(1L);
    }

    @Test
    void testIsValidBanner_EmptyList_ReturnsTrue() {
        when(bannersRepository.findByLanguage("en")).thenReturn(Collections.emptyList());

        boolean result = jpaBannersAdapter.isValidBanner("en", BannerType.IMAGE, "tx001");

        assertTrue(result);
    }

    @Test
    void testIsValidBanner_LessThanFiveSameType_ReturnsTrue() {
        List<BannersEntity> banners = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            BannersEntity b = new BannersEntity();
            b.setType(BannerType.IMAGE);
            banners.add(b);
        }

        when(bannersRepository.findByLanguage("en")).thenReturn(banners);

        boolean result = jpaBannersAdapter.isValidBanner("en", BannerType.IMAGE, "tx001");

        assertTrue(result);
    }

    @Test
    void testIsValidBanner_MixedTypes_ReturnsFalse() {
        BannersEntity b1 = new BannersEntity();
        b1.setType(BannerType.IMAGE);
        BannersEntity b2 = new BannersEntity();
        b2.setType(BannerType.VIDEO);

        when(bannersRepository.findByLanguage("en")).thenReturn(List.of(b1, b2));

        boolean result = jpaBannersAdapter.isValidBanner("en", BannerType.IMAGE, "tx001");

        assertFalse(result);
    }

    @Test
    void testIsValidBanner_TooManyBanners_ReturnsFalse() {
        List<BannersEntity> banners = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            BannersEntity b = new BannersEntity();
            b.setType(BannerType.IMAGE);
            banners.add(b);
        }

        when(bannersRepository.findByLanguage("en")).thenReturn(banners);

        boolean result = jpaBannersAdapter.isValidBanner("en", BannerType.IMAGE, "tx001");

        assertFalse(result);
    }

    @Test
    void testSave() {
        BannersEntity banner = new BannersEntity();
        jpaBannersAdapter.save(banner);
        verify(bannersRepository).save(banner);
    }

    @Test
    void testDelete() {
        BannersEntity banner = new BannersEntity();
        jpaBannersAdapter.delete(banner);
        verify(bannersRepository).delete(banner);
    }
}
