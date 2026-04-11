package com.register.wowlibre.infrastructure.repositories.notifications;

import com.register.wowlibre.infrastructure.entities.NotificationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    default List<NotificationEntity> findAllOrderByCreatedAtDesc() {
        return findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /** Globales (recipient null) + dirigidas al usuario. */
    @Query("SELECT n FROM NotificationEntity n WHERE n.recipientUserId IS NULL OR n.recipientUserId = :userId ORDER BY n.createdAt DESC")
    List<NotificationEntity> findVisibleForUser(@Param("userId") Long userId);
}
