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

    public Optional<NotificationProvidersEntity> findByType(String name, String transactionId) {
        return notificationProvidersRepository.findByType(name);
    }

    @Override
    public Optional<NotificationProvidersEntity> findById(Long id, String transactionId) {
        return notificationProvidersRepository.findById(id);
    }

    @Override
    public List<NotificationProvidersEntity> findAll(String transactionId) {
        return notificationProvidersRepository.findAll();
    }

    @Override
    public void save(NotificationProvidersEntity notificationProvider, String transactionId) {
        notificationProvidersRepository.save(notificationProvider);
    }

    @Override
    public void delete(NotificationProvidersEntity notificationProvider, String transactionId) {
        notificationProvidersRepository.delete(notificationProvider);
    }
}
