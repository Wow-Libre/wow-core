package com.register.wowlibre.infrastructure.repositories.new_sections;

import com.register.wowlibre.domain.port.out.news_sections.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaNewSectionsAdapter implements ObtainNewsSections, SaveNewsSections, DeleteNewsSections {
    private final NewsSectionsRepository newsSectionsRepository;

    public JpaNewSectionsAdapter(NewsSectionsRepository newsSectionsRepository) {
        this.newsSectionsRepository = newsSectionsRepository;
    }

    @Override
    public List<NewsSectionsEntity> findByNewsIdOrderBySectionOrderAsc(NewsEntity newsEntity) {
        return newsSectionsRepository.findByNewsIdOrderBySectionOrderAsc(newsEntity);
    }

    @Override
    public Integer countBySelectOrder(NewsEntity newsEntity) {
        return newsSectionsRepository.findMaxSectionOrderByNewsId(newsEntity.getId());
    }

    @Override
    public Optional<NewsSectionsEntity> findByIdAndNewsEntity(Long id, NewsEntity newsEntity) {
        return newsSectionsRepository.findByIdAndNewsId(id, newsEntity);
    }

    @Override
    public void save(NewsSectionsEntity newsSectionsEntity) {
        newsSectionsRepository.save(newsSectionsEntity);
    }

    @Override
    public void delete(NewsSectionsEntity newsSectionsEntity) {
        newsSectionsRepository.delete(newsSectionsEntity);
    }
}
