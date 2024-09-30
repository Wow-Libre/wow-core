package com.register.wowlibre.domain.port.in.rol;

import com.register.wowlibre.domain.model.*;

public interface RolPort {
    RolModel findByName(String name, String transactionId);
}
