package com.register.wowlibre.domain.port.out.news_sections;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainNewsSections {
    List<NewsSectionsEntity> findByNewsIdOrderBySectionOrderAsc(NewsEntity newsEntity);

    Integer countBySelectOrder(NewsEntity newsEntity);

    Optional<NewsSectionsEntity> findByIdAndNewsEntity(Long id, NewsEntity newsEntity);
}
