package com.register.wowlibre.domain.port.in.integrator;

import com.register.wowlibre.domain.model.*;

public interface IntegratorPort {
    Long create(String username, String password, boolean rebuildUsername, ServerModel server, UserModel userModel,
                String transactionId);
}
