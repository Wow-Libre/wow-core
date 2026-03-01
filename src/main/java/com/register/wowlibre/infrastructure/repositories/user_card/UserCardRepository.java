package com.register.wowlibre.infrastructure.repositories.user_card;

import com.register.wowlibre.infrastructure.entities.UserCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCardRepository extends JpaRepository<UserCardEntity, Long> {

    List<UserCardEntity> findByUserIdOrderByObtainedAtDesc(Long userId);
}
