package com.register.wowlibre.domain.port.out.news;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface ObtainNews {
    Page<NewsEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    Optional<NewsEntity> findById(Long id);
}
