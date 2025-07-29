package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.server_resources.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaServerResourcesAdapterTest {

    private ServerResourcesRepository repository;
    private JpaServerResourcesAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ServerResourcesRepository.class);
        adapter = new JpaServerResourcesAdapter(repository);
    }

    @Test
    void testFindByServerId() {
        // Arrange
        RealmEntity realm = new RealmEntity(); // mock o instancia vacía
        RealmResourcesEntity resource1 = new RealmResourcesEntity();
        RealmResourcesEntity resource2 = new RealmResourcesEntity();
        List<RealmResourcesEntity> expectedResources = List.of(resource1, resource2);

        when(repository.findByRealmId(realm)).thenReturn(expectedResources);

        // Act
        List<RealmResourcesEntity> result = adapter.findByServerId(realm, "tx123");

        // Assert
        assertEquals(2, result.size());
        assertSame(expectedResources, result);
        verify(repository).findByRealmId(realm);
    }
}
