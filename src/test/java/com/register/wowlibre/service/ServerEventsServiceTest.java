package com.register.wowlibre.service;

import com.register.wowlibre.application.services.server_events.*;
import com.register.wowlibre.domain.port.out.server_events.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerEventsServiceTest {

    private ObtainServerEvents obtainServerEvents;
    private ServerEventsService serverEventsService;

    @BeforeEach
    void setUp() {
        obtainServerEvents = mock(ObtainServerEvents.class);
        serverEventsService = new ServerEventsService(obtainServerEvents);
    }

    @Test
    void testFindByServerId_ReturnsEvents() {
        // Arrange
        RealmEntity realm = new RealmEntity(); // Mock real si tiene lógica interna
        RealmEventsEntity event1 = new RealmEventsEntity();
        event1.setId(1L);
        event1.setTitle("Event 1");

        RealmEventsEntity event2 = new RealmEventsEntity();
        event2.setId(2L);
        event2.setTitle("Event 2");

        List<RealmEventsEntity> expected = List.of(event1, event2);

        when(obtainServerEvents.findByServerId(realm, "tx456")).thenReturn(expected);

        // Act
        List<RealmEventsEntity> result = serverEventsService.findByServerId(realm, "tx456");

        // Assert
        assertEquals(2, result.size());
        assertEquals("Event 1", result.get(0).getTitle());
        verify(obtainServerEvents).findByServerId(realm, "tx456");
    }
}
