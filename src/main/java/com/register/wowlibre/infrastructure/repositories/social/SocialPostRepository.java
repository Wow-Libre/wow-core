package com.register.wowlibre.infrastructure.repositories.social;

import com.register.wowlibre.infrastructure.entities.social.SocialPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SocialPostRepository extends PagingAndSortingRepository<SocialPostEntity, Long>, CrudRepository<SocialPostEntity, Long> {

    Page<SocialPostEntity> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    Optional<SocialPostEntity> findByIdAndDeletedAtIsNull(Long id);

    /** Publicaciones no eliminadas del usuario desde {@code since} (ventana móvil 24 h). */
    long countByUserIdAndDeletedAtIsNullAndCreatedAtGreaterThanEqual(Long userId, LocalDateTime since);
}
