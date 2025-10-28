package com.register.wowlibre.service;

import com.register.wowlibre.application.services.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.out.notification_provider.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificationProviderServiceTest {

    private ObtainNotificationProvider obtainNotificationProvider;
    private SaveNotificationProvider saveNotificationProvider;
    private NotificationProviderService service;

    @BeforeEach
    void setUp() {
        obtainNotificationProvider = mock(ObtainNotificationProvider.class);
        saveNotificationProvider = mock(SaveNotificationProvider.class);
        service = new NotificationProviderService(obtainNotificationProvider, saveNotificationProvider);
    }

    @Test
    void findByName_shouldReturnProvider() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();
        when(obtainNotificationProvider.findByType("EMAIL", "tx")).thenReturn(Optional.of(entity));

        Optional<NotificationProvidersEntity> result = service.findByName("EMAIL", "tx");

        assertTrue(result.isPresent());
        verify(obtainNotificationProvider).findByType("EMAIL", "tx");
    }

    @Test
    void save_shouldCallSaveProvider() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();
        service.save(entity, "tx");
        verify(saveNotificationProvider).save(entity, "tx");
    }

    @Test
    void configProvider_shouldSaveNewProvider() {
        CreateNotificationProviderDto dto = new CreateNotificationProviderDto();
        dto.setName(NotificationType.MAILS.name());
        dto.setHost("host");
        dto.setClient("client");
        dto.setSecret("secret");

        when(obtainNotificationProvider.findByType(NotificationType.MAILS.name(), "tx")).thenReturn(Optional.empty());

        service.configProvider(dto, "tx");

        verify(saveNotificationProvider).save(any(NotificationProvidersEntity.class), eq("tx"));
    }

    @Test
    void configProvider_shouldThrowIfTypeInvalid() {
        CreateNotificationProviderDto dto = new CreateNotificationProviderDto();
        dto.setName("INVALID");

        Exception ex = assertThrows(InternalException.class, () -> service.configProvider(dto, "tx"));
        assertTrue(ex.getMessage().contains("not valid"));
    }

    @Test
    void configProvider_shouldThrowIfProviderExists() {
        CreateNotificationProviderDto dto = new CreateNotificationProviderDto();
        dto.setName(NotificationType.MAILS.name());

        when(obtainNotificationProvider.findByType(NotificationType.MAILS.name(), "tx"))
                .thenReturn(Optional.of(new NotificationProvidersEntity()));

        Exception ex = assertThrows(InternalException.class, () -> service.configProvider(dto, "tx"));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void deleteProvider_shouldDeleteIfExists() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();
        when(obtainNotificationProvider.findById(1L, "tx")).thenReturn(Optional.of(entity));

        service.deleteProvider(1L, "tx");

        verify(saveNotificationProvider).delete(entity, "tx");
    }

    @Test
    void deleteProvider_shouldThrowIfNotExists() {
        when(obtainNotificationProvider.findById(1L, "tx")).thenReturn(Optional.empty());

        Exception ex = assertThrows(InternalException.class, () -> service.deleteProvider(1L, "tx"));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void allProviders_shouldReturnList() {
        NotificationProvidersEntity entity = new NotificationProvidersEntity();
        entity.setId(1L);
        entity.setType("EMAIL");
        entity.setClient("client");
        entity.setHost("host");
        entity.setSecretKey("secret");

        when(obtainNotificationProvider.findAll("tx")).thenReturn(List.of(entity));

        List<NotificationProviderModel> result = service.allProviders("tx");

        assertEquals(1, result.size());
        assertEquals("EMAIL", result.get(0).getName());
    }
}
