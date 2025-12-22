package com.register.wowlibre.infrastructure.client;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.shared.*;
import org.slf4j.*;
import org.springframework.core.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@Component
public class TransactionClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionClient.class);
    private final RestTemplate restTemplate;

    public TransactionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isActiveSubscription(String token, String transactionId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_TRANSACTION_ID, transactionId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GenericResponse<Boolean>> response = restTemplate.exchange(String.format("%s/transaction" +
                    "/api/subscription", ""), HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[TransactionClient] [isActiveSubscription] Client/Server Error: {}.  HTTP Status: {},  " +
                    "Body: {}", e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(), transactionId);
        } catch (Exception e) {
            LOGGER.error("[TransactionClient] [isActiveSubscription] Unexpected Error: {} - TransactionId{}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

}
