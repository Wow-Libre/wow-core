package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.server_details.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaServerDetailsAdapterTest {

    private ServerDetailsRepository serverDetailsRepository;
    private JpaServerDetailsAdapter adapter;

    @BeforeEach
    void setUp() {
        serverDetailsRepository = mock(ServerDetailsRepository.class);
        adapter = new JpaServerDetailsAdapter(serverDetailsRepository);
    }

    @Test
    void testFindByServerId_ReturnsList() {
        RealmEntity realm = new RealmEntity();
        RealmDetailsEntity detail1 = new RealmDetailsEntity();
        RealmDetailsEntity detail2 = new RealmDetailsEntity();
        List<RealmDetailsEntity> expectedDetails = List.of(detail1, detail2);

        when(serverDetailsRepository.findByRealmId(realm)).thenReturn(expectedDetails);

        List<RealmDetailsEntity> result = adapter.findByServerId(realm, "tx001");

        assertEquals(2, result.size());
        assertSame(detail1, result.get(0));
        verify(serverDetailsRepository).findByRealmId(realm);
    }

    @Test
    void testDelete_CallsRepository() {
        RealmDetailsEntity detail = new RealmDetailsEntity();

        adapter.delete(detail, "tx002");

        verify(serverDetailsRepository).delete(detail);
    }
}
