package com.register.wowlibre.domain.port.out.news;

import com.register.wowlibre.infrastructure.entities.*;

public interface DeleteNews {
    void delete(NewsEntity newsEntity);
}
