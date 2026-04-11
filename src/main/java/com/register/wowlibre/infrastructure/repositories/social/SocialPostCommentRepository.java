package com.register.wowlibre.infrastructure.repositories.social;

import com.register.wowlibre.infrastructure.entities.social.SocialPostCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SocialPostCommentRepository extends PagingAndSortingRepository<SocialPostCommentEntity, Long>, CrudRepository<SocialPostCommentEntity, Long> {

    Page<SocialPostCommentEntity> findByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long postId, Pageable pageable);

    long countByPostIdAndDeletedAtIsNull(Long postId);
}
