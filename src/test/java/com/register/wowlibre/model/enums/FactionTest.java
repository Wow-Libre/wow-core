package com.register.wowlibre.model.enums;

import com.register.wowlibre.domain.enums.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class FactionTest {

    @Test
    void testFromStringWithValidValues() {
        assertEquals(Faction.HORDE, Faction.fromString("HORDE"));
        assertEquals(Faction.ALLIANCE, Faction.fromString("alliance"));
        assertEquals(Faction.ALL, Faction.fromString("All"));
    }

    @Test
    void testFromStringWithNullOrBlank() {
        assertEquals(Faction.ALL, Faction.fromString(null));
        assertEquals(Faction.ALL, Faction.fromString(""));
        assertEquals(Faction.ALL, Faction.fromString("   "));
    }

    @Test
    void testFromStringWithInvalidValue() {
        assertEquals(Faction.ALL, Faction.fromString("neutral"));
        assertEquals(Faction.ALL, Faction.fromString("test"));
        assertEquals(Faction.ALL, Faction.fromString("123"));
    }
}
