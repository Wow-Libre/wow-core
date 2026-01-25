package com.register.wowlibre.infrastructure.repositories.payu_credentials;

import com.register.wowlibre.domain.port.out.payu_credentials.ObtainPayuCredentials;
import com.register.wowlibre.domain.port.out.payu_credentials.SavePayuCredentials;
import com.register.wowlibre.infrastructure.entities.transactions.PayuCredentialsEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPayuCredentialsAdapter implements ObtainPayuCredentials, SavePayuCredentials {
    private final PayuCredentialsRepository payuCredentialsRepository;

    public JpaPayuCredentialsAdapter(PayuCredentialsRepository payuCredentialsRepository) {
        this.payuCredentialsRepository = payuCredentialsRepository;
    }

    @Override
    public Optional<PayuCredentialsEntity> findByGatewayId(Long gatewayId, String transactionId) {
        return payuCredentialsRepository.findByGatewayId(gatewayId);
    }

    @Override
    public PayuCredentialsEntity save(PayuCredentialsEntity payuCredentialsEntity) {
        return payuCredentialsRepository.save(payuCredentialsEntity);
    }
}
