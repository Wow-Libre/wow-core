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
public class CommunicationsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationsClient.class);
    private final RestTemplate restTemplate;

    public CommunicationsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMailBasic(String host, String client, SendMailCommunicationRequest request, String transactionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_TRANSACTION_ID, transactionId);
        headers.set(HEADER_CLIENT, client);

        HttpEntity<SendMailCommunicationRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(String.format("%s" +
                            "/api/send/mail/basic",
                    host), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });

            if (response.getStatusCode().is2xxSuccessful()) {
                Objects.requireNonNull(response.getBody());
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[CommunicationsClient] [sendMailBasic] Client/Server Error: {}. The request failed with a " +
                            "client or realm error. " +
                            "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[CommunicationsClient] [sendMailBasic] Unexpected Error: {}. An unexpected error occurred " +
                            "during the transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }
        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void sendMailTemplate(String host, String client, SendMailTemplateRequest request,
                                 String transactionId) {
        HttpHeaders headers = new HttpHeaders();
        if (transactionId != null) {
            headers.set(HEADER_TRANSACTION_ID, transactionId);
        }
        headers.set(HEADER_CLIENT, client);

        HttpEntity<SendMailTemplateRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(String.format("%s" +
                            "/api/send/mail/template",
                    host), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });

            if (response.getStatusCode().is2xxSuccessful()) {
                Objects.requireNonNull(response.getBody());
                return;
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[CommunicationsClient] [sendMailTemplate] Client/Server Error: {}. The request failed with " +
                            "a client or realm error. HTTP Status: {}, Response Body: {}", e.getMessage(),
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[CommunicationsClient] [sendMailTemplate] Unexpected Error: {}. An unexpected error " +
                    "occurred during the transaction with ID: {}.", e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }
        throw new InternalException("Unexpected transaction failure", transactionId);
    }

}
