package com.register.wowlibre.domain.port.out.news;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveNews {
    void save(NewsEntity newsEntity);
}
