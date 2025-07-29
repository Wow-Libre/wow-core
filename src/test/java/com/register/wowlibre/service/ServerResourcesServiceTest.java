package com.register.wowlibre.service;

import com.register.wowlibre.application.services.server_resources.*;
import com.register.wowlibre.domain.port.out.server_resources.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerResourcesServiceTest {
    private ObtainServerResources obtainServerResources;
    private ServerResourcesService serverResourcesService;

    @BeforeEach
    void setUp() {
        obtainServerResources = mock(ObtainServerResources.class);
        serverResourcesService = new ServerResourcesService(obtainServerResources);
    }

    @Test
    void testFindByServerId_ReturnsResources() {
        RealmEntity realm = new RealmEntity();
        String transactionId = "tx123";

        RealmResourcesEntity resource1 = new RealmResourcesEntity();
        RealmResourcesEntity resource2 = new RealmResourcesEntity();

        List<RealmResourcesEntity> mockResources = List.of(resource1, resource2);

        when(obtainServerResources.findByServerId(realm, transactionId)).thenReturn(mockResources);

        List<RealmResourcesEntity> result = serverResourcesService.findByServerId(realm, transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(obtainServerResources, times(1)).findByServerId(realm, transactionId);
    }

    @Test
    void testFindByServerId_EmptyResult() {
        RealmEntity realm = new RealmEntity();
        String transactionId = "txEmpty";

        when(obtainServerResources.findByServerId(realm, transactionId)).thenReturn(List.of());

        List<RealmResourcesEntity> result = serverResourcesService.findByServerId(realm, transactionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(obtainServerResources, times(1)).findByServerId(realm, transactionId);
    }
}
