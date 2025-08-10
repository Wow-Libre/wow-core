package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.notification_providers.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaNotificationProvidersAdapterTest {
    private NotificationProvidersRepository repository;
    private JpaNotificationProvidersAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(NotificationProvidersRepository.class);
        adapter = new JpaNotificationProvidersAdapter(repository);
    }

    @Test
    void findByType_shouldReturnOptionalEntity() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();
        when(repository.findByType("EMAIL")).thenReturn(Optional.of(entity));

        Optional<NotificationProvidersEntity> result = adapter.findByType("EMAIL", "tx");

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(repository).findByType("EMAIL");
    }

    @Test
    void findById_shouldReturnOptionalEntity() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<NotificationProvidersEntity> result = adapter.findById(1L, "tx");

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(repository).findById(1L);
    }

    @Test
    void findAll_shouldReturnList() {
        List<NotificationProvidersEntity> entities = List.of(new NotificationProvidersEntity());
        when(repository.findAll()).thenReturn(entities);

        List<NotificationProvidersEntity> result = adapter.findAll("tx");

        assertEquals(entities, result);
        verify(repository).findAll();
    }

    @Test
    void save_shouldCallRepositorySave() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();

        adapter.save(entity, "tx");

        verify(repository).save(entity);
    }

    @Test
    void delete_shouldCallRepositoryDelete() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();

        adapter.delete(entity, "tx");

        verify(repository).delete(entity);
    }
}
