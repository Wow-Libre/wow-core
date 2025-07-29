package com.register.wowlibre.model.enums;

import com.register.wowlibre.domain.enums.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BannerTypeTest {

    @Test
    void testFromNameWithValidValues() {
        assertEquals(BannerType.IMAGE, BannerType.fromName("IMAGE"));
        assertEquals(BannerType.IMAGE, BannerType.fromName("image"));
        assertEquals(BannerType.VIDEO, BannerType.fromName("VIDEO"));
        assertEquals(BannerType.VIDEO, BannerType.fromName("video"));
    }

    @Test
    void testFromNameWithInvalidValues() {
        assertNull(BannerType.fromName("banner"));
        assertNull(BannerType.fromName("123"));
        assertNull(BannerType.fromName("vid"));
    }

    @Test
    void testFromNameWithNullOrBlank() {
        assertNull(BannerType.fromName(null));
        assertNull(BannerType.fromName(""));
        assertNull(BannerType.fromName("   "));
    }
}
