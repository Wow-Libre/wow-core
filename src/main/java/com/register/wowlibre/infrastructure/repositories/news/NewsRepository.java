package com.register.wowlibre.infrastructure.repositories.news;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.*;

public interface NewsRepository extends CrudRepository<NewsEntity, Long> {
    Page<NewsEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}
