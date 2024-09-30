package com.register.wowlibre.domain.port.out.server;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveServerPort {
    void save(ServerEntity serverEntity, String transactionId);
}
