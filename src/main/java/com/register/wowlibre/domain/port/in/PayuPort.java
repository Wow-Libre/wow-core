package com.register.wowlibre.domain.port.in;


import com.register.wowlibre.domain.dto.client.payu.*;

public interface PayuPort {
    PayUOrderDetailResponse getOrderDetailByReference(String referenceCode, String apiLogin,
                                                      String apiKey);
}
