package com.register.wowlibre.model.enums;

import com.register.wowlibre.domain.enums.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class WowFactionRaceTest {
    @Test
    void testGetByIdWithKnownIds() {
        assertEquals(WowFactionRace.HUMAN, WowFactionRace.getById(1L));
        assertEquals(WowFactionRace.TAUREN, WowFactionRace.getById(6L));
        assertEquals(WowFactionRace.LIGHTFORGED_DRAENEI, WowFactionRace.getById(30L));
        assertEquals(WowFactionRace.UNKNOWN.getFaction(), WowFactionRace.getById(0L).getFaction());
    }

    @Test
    void testGetByIdWithUnknownIdReturnsUnknown() {
        WowFactionRace result = WowFactionRace.getById(999L);
        assertEquals(WowFactionRace.UNKNOWN, result);
        assertEquals(Faction.ALL, result.getFaction());
    }

    @Test
    void testGetByIdWithNegativeIdReturnsUnknown() {
        assertEquals(WowFactionRace.UNKNOWN, WowFactionRace.getById(-1L));
    }

    @Test
    void testGetByIdWithNullThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            WowFactionRace.getById(null);
        });
    }
}
