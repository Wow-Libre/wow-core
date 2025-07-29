package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.server_events.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JpaServerEventsAdapterTest {
    private ServerEventsRepository repository;
    private JpaServerEventsAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ServerEventsRepository.class);
        adapter = new JpaServerEventsAdapter(repository);
    }

    @Test
    void testFindByServerId() {
        // Arrange
        RealmEntity realm = new RealmEntity(); // Puedes usar mock también si hay lógica compleja
        RealmEventsEntity event1 = new RealmEventsEntity();
        event1.setId(1L);
        event1.setTitle("Title 1");

        RealmEventsEntity event2 = new RealmEventsEntity();
        event2.setId(2L);
        event2.setTitle("Title 2");

        List<RealmEventsEntity> expectedEvents = List.of(event1, event2);

        when(repository.findByRealmId(realm)).thenReturn(expectedEvents);

        // Act
        List<RealmEventsEntity> result = adapter.findByServerId(realm, "tx123");

        // Assert
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).getTitle());
        assertEquals("Title 2", result.get(1).getTitle());
        verify(repository).findByRealmId(realm);
    }
}
