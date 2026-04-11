package com.register.wowlibre.infrastructure.repositories.social;

import com.register.wowlibre.infrastructure.entities.social.SocialPostMediaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SocialPostMediaRepository extends CrudRepository<SocialPostMediaEntity, Long> {

    List<SocialPostMediaEntity> findByPostIdOrderBySortOrderAsc(Long postId);
}
