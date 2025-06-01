package com.register.wowlibre.infrastructure.repositories.news;

import com.register.wowlibre.domain.port.out.news.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaNewsAdapter implements ObtainNews, SaveNews, DeleteNews {

    private final NewsRepository newsRepository;

    public JpaNewsAdapter(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public Page<NewsEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable) {
        return newsRepository.findAllByOrderByUpdatedAtDesc(pageable);
    }

    @Override
    public Optional<NewsEntity> findById(Long id) {
        return newsRepository.findById(id);
    }

    @Override
    public void save(NewsEntity newsEntity) {
        newsRepository.save(newsEntity);
    }

    @Override
    public void delete(NewsEntity newsEntity) {
        newsRepository.delete(newsEntity);
    }
}
