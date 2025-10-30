package com.register.wowlibre.domain.port.in.rol;

import com.register.wowlibre.infrastructure.entities.*;

public interface RolPort {
    RolEntity findByName(String name, String transactionId);
}
