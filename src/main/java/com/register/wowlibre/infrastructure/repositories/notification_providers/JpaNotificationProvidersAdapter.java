package com.register.wowlibre.infrastructure.repositories.notification_providers;

import com.register.wowlibre.domain.port.out.notification_provider.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaNotificationProvidersAdapter implements ObtainNotificationProvider, SaveNotificationProvider {
    private final NotificationProvidersRepository notificationProvidersRepository;

    public JpaNotificationProvidersAdapter(NotificationProvidersRepository notificationProvidersRepository) {
        this.notificationProvidersRepository = notificationProvidersRepository;
    }

    @Override
    public Optional<NotificationProvidersEntity> findByName(String name, String transactionId) {
        return notificationProvidersRepository.findByName(name);
    }

    @Override
    public void save(NotificationProvidersEntity notificationProvider, String transactionId) {
        notificationProvidersRepository.save(notificationProvider);
    }
}
