package com.register.wowlibre.infrastructure.repositories.new_sections;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface NewsSectionsRepository extends CrudRepository<NewsSectionsEntity, Long> {
    List<NewsSectionsEntity> findByNewsIdOrderBySectionOrderAsc(NewsEntity newsEntity);

    @Query("SELECT MAX(s.sectionOrder) FROM NewsSectionsEntity s WHERE s.newsId.id = :newsId")
    Integer findMaxSectionOrderByNewsId(@Param("newsId") Long newsId);

    Optional<NewsSectionsEntity> findByIdAndNewsId(Long id, NewsEntity newsEntity);
}
