package com.register.wowlibre.application.services.auth;

import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.port.in.auth_integrator.*;
import com.register.wowlibre.infrastructure.client.*;
import org.springframework.stereotype.*;

@Service
public class AuthIntegratorService implements AuthIntegratorPort {

    private final AuthIntegratorClient authIntegratorClient;

    public AuthIntegratorService(AuthIntegratorClient authIntegratorClient) {
        this.authIntegratorClient = authIntegratorClient;
    }

    @Override
    public AuthClientResponse auth(String host, String username, String password, String transactionId) {
        return authIntegratorClient.auth(host, new AuthClientRequest(username, password), transactionId);
    }

    @Override
    public void create(String host, String username, String password, Long realmId, String emulator,
                       Integer expansionId, String transactionId) {
        authIntegratorClient.create(host, new AuthClientCreateRequest(username, password, realmId, emulator,
                expansionId), transactionId);
    }

    @Override
    public void inactiveUser(String host, String jwt, String transactionId) {
        authIntegratorClient.inactive(host, jwt, transactionId);
    }
}
