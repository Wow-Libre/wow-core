package com.register.wowlibre.infrastructure.client;

import com.register.wowlibre.domain.dto.client.SendMailCommunicationRequest;
import com.register.wowlibre.domain.dto.client.SendMailTemplateRequest;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.shared.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static com.register.wowlibre.domain.constant.Constants.HEADER_CLIENT;
import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;

@Component
public class CommunicationsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationsClient.class);
    private final RestTemplate restTemplate;

    public CommunicationsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders buildHeaders(String transactionId, String client) {
        HttpHeaders headers = new HttpHeaders();
        if (transactionId != null) {
            headers.set(HEADER_TRANSACTION_ID, transactionId);
        }
        if (client != null) {
            headers.set(HEADER_CLIENT, client);
        }
        return headers;
    }

    public void sendMailBasic(String host, String client, SendMailCommunicationRequest request, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, client);
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
