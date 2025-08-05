package com.register.wowlibre.application.services.notification_provider;

import com.register.wowlibre.domain.port.in.notification_provider.*;
import com.register.wowlibre.domain.port.out.notification_provider.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class NotificationProviderService implements NotificationProviderPort {

    private final ObtainNotificationProvider obtainNotificationProvider;
    private final SaveNotificationProvider saveNotificationProvider;

    public NotificationProviderService(ObtainNotificationProvider obtainNotificationProvider,
                                       SaveNotificationProvider saveNotificationProvider) {
        this.obtainNotificationProvider = obtainNotificationProvider;
        this.saveNotificationProvider = saveNotificationProvider;
    }

    @Override
    public Optional<NotificationProvidersEntity> findByName(String name, String transactionId) {
        return obtainNotificationProvider.findByName(name, transactionId);
    }

    @Override
    public void save(NotificationProvidersEntity notificationProvider, String transactionId) {
        saveNotificationProvider.save(notificationProvider, transactionId);
    }
}
