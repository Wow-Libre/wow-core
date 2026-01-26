package com.register.wowlibre.infrastructure.repositories.payu_credentials;

import com.register.wowlibre.domain.port.out.payu_credentials.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaPayuCredentialsAdapter implements ObtainPayuCredentials, SavePayuCredentials {
    private final PayuCredentialsRepository payUCredentialsRepository;

    public JpaPayuCredentialsAdapter(PayuCredentialsRepository payUCredentialsRepository) {
        this.payUCredentialsRepository = payUCredentialsRepository;
    }

    @Override
    public Optional<PayuCredentialsEntity> findByPayUCredentials(Long gatewayId, String transactionId) {
        return payUCredentialsRepository.findByGatewayId(gatewayId);
    }

    @Override
    public void save(PayuCredentialsEntity payuCredentialsEntity, String transactionId) {
        payUCredentialsRepository.save(payuCredentialsEntity);
    }

    @Override
    public void delete(PayuCredentialsEntity payuCredentials, String transactionId) {
        payUCredentialsRepository.delete(payuCredentials);
    }
}
