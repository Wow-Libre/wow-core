package com.register.wowlibre.infrastructure.repositories.notifications;

import com.register.wowlibre.infrastructure.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Sort;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    default List<NotificationEntity> findAllOrderByCreatedAtDesc() {
        return findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
