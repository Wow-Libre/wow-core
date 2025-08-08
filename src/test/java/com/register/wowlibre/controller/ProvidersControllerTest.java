package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.notification_provider.*;
import com.register.wowlibre.infrastructure.controller.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProvidersControllerTest {

    private NotificationProviderPort notificationProviderPort;
    private ProvidersController controller;

    @BeforeEach
    void setUp() {
        notificationProviderPort = mock(NotificationProviderPort.class);
        controller = new ProvidersController(notificationProviderPort);
    }

    @Test
    void save_shouldCallConfigProviderAndReturnOk() {
        CreateNotificationProviderDto dto = new CreateNotificationProviderDto();
        String transactionId = "tx-1";

        ResponseEntity<?> response = controller.save(transactionId, dto);

        verify(notificationProviderPort).configProvider(dto, transactionId);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void delete_shouldCallDeleteProviderAndReturnOk() {
        Long providerId = 1L;
        String transactionId = "tx-2";

        ResponseEntity<?> response = controller.delete(transactionId, providerId);

        verify(notificationProviderPort).deleteProvider(providerId, transactionId);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void all_shouldReturnProvidersList() {
        String transactionId = "tx-3";
        List<NotificationProviderModel> providers = Collections.emptyList();
        when(notificationProviderPort.allProviders(transactionId)).thenReturn(providers);

        ResponseEntity<?> response = controller.all(transactionId);

        verify(notificationProviderPort).allProviders(transactionId);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
    }
}
