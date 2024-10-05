package com.register.wowlibre.infrastructure.client;

import com.register.wowlibre.domain.dto.client.*;
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
public class IntegratorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegratorClient.class);

    private final RestTemplate restTemplate;

    public IntegratorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public long createAccountGame(String host, AccountGameCreateDto request, String transactionId) {
        HttpHeaders headers = new HttpHeaders();

        headers.set(HEADER_TRANSACTION_ID, transactionId);
        HttpEntity<AccountGameCreateDto> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<GenericResponse<Long>> response = restTemplate.exchange(String.format("%s/api/account"
                                    + "/create",
                            host),
                    HttpMethod.POST, entity,
                    new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("Client/Server Error: {}. The request failed with a client or server error. " +
                            "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or server error", transactionId);
        } catch (Exception e) {
            LOGGER.error("Unexpected Error: {}. An unexpected error occurred during the transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);

    }
}
