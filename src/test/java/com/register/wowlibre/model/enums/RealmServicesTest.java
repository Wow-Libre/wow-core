package com.register.wowlibre.model.enums;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class RealmServicesTest {
    @Test
    void testGetNameWithValidValues() {
        assertEquals(RealmServices.BANK, RealmServices.getName("BANK", "tx123"));
        assertEquals(RealmServices.SEND_LEVEL, RealmServices.getName("SEND_LEVEL", "tx456"));
    }

    @Test
    void testGetNameWithInvalidValueThrowsException() {
        String transactionId = "tx789";
        InternalException exception = assertThrows(InternalException.class, () ->
                RealmServices.getName("INVALID", transactionId)
        );
        assertEquals("The realm service with name INVALID was not found.", exception.getMessage());
        assertEquals(transactionId, exception.transactionId);
    }

    @Test
    void testGetNameWithNullValueThrowsException() {
        String transactionId = "tx000";
        InternalException exception = assertThrows(InternalException.class, () ->
                RealmServices.getName(null, transactionId)
        );
        assertEquals("The realm service with name null was not found.", exception.getMessage());
        assertEquals(transactionId, exception.transactionId);
    }

}
