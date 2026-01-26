package com.register.wowlibre.application.services.payu;

import com.register.wowlibre.domain.dto.client.payu.*;
import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.infrastructure.client.*;
import org.springframework.stereotype.*;

@Service
public class PayUService implements PayuPort {
    private final PayUClient payUClient;

    public PayUService(PayUClient payUClient) {
        this.payUClient = payUClient;
    }

    @Override
    public PayUOrderDetailResponse getOrderDetailByReference(String referenceCode, String apiLogin,
                                                             String apiKey) {
        return payUClient.getOrderDetailByReference(referenceCode, apiLogin, apiKey);
    }
}
