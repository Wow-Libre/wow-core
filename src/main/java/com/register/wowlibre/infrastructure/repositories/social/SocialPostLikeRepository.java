package com.register.wowlibre.infrastructure.repositories.social;

import com.register.wowlibre.infrastructure.entities.social.SocialPostLikeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SocialPostLikeRepository extends CrudRepository<SocialPostLikeEntity, Long> {

    Optional<SocialPostLikeEntity> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    void deleteByPostIdAndUserId(Long postId, Long userId);
}
