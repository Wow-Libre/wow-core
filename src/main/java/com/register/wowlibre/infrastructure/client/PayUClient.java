package com.register.wowlibre.infrastructure.client;

import com.register.wowlibre.domain.dto.client.payu.*;
import com.register.wowlibre.infrastructure.config.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Component
public class PayUClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayUClient.class);

    private final RestTemplate restTemplate;
    private final Configurations configurations;

    public PayUClient(RestTemplate restTemplate, Configurations configurations) {
        this.restTemplate = restTemplate;
        this.configurations = configurations;
    }

    public PayUOrderDetailResponse getOrderDetailByReference(String referenceCode, String apiLogin,
                                                             String apiKey) {
        try {
            LOGGER.info("Getting order detail for reference code: {}", referenceCode);

            PayUOrderDetailRequest request = PayUOrderDetailRequest.builder()
                    .language("es")
                    .command("ORDER_DETAIL_BY_REFERENCE_CODE")
                    .merchant(PayUOrderDetailRequest.PayUMerchant.builder()
                            .apiLogin(apiLogin)
                            .apiKey(apiKey)
                            .build())
                    .details(PayUOrderDetailRequest.PayUDetails.builder()
                            .referenceCode(referenceCode)
                            .build())
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");

            HttpEntity<PayUOrderDetailRequest> entity = new HttpEntity<>(request, headers);

            PayUOrderDetailResponse response = restTemplate.postForObject(
                    configurations.getPathPayU(),
                    entity,
                    PayUOrderDetailResponse.class);

            LOGGER.info("Successfully retrieved order detail for reference code: {}", referenceCode);
            return response;

        } catch (RestClientException e) {
            LOGGER.error("Error getting order detail for reference code: {}", referenceCode, e);
            throw new RuntimeException("Error communicating with PayU API", e);
        }
    }
}
