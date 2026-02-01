package com.register.wowlibre.infrastructure.repositories.payment_gateways;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface PaymentGatewaysRepository extends CrudRepository<PaymentGatewaysEntity, Long> {
    Optional<PaymentGatewaysEntity> findByTypeAndIsActiveIsTrue(PaymentType type);

    List<PaymentGatewaysEntity> findByIsActiveIsTrue();
}
