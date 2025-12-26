package com.register.wowlibre.infrastructure.client;

import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.shared.*;
import org.slf4j.*;
import org.springframework.core.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;
import org.springframework.web.util.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@Component
public class IntegratorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegratorClient.class);

    private final RestTemplate restTemplate;

    public IntegratorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Construye HttpHeaders con transactionId y Authorization de forma segura.
     * Solo agrega el header transaction_id si el valor no es null.
     */
    private HttpHeaders buildHeaders(String transactionId, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        if (transactionId != null) {
            headers.set(HEADER_TRANSACTION_ID, transactionId);
        }
        if (jwt != null) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        }
        return headers;
    }

    /**
     * Construye HttpHeaders solo con transactionId de forma segura.
     */
    private HttpHeaders buildHeaders(String transactionId) {
        HttpHeaders headers = new HttpHeaders();
        if (transactionId != null) {
            headers.set(HEADER_TRANSACTION_ID, transactionId);
        }
        return headers;
    }

    public long createAccountGame(String host, AccountGameCreateRequest request, String transactionId) {

        HttpHeaders headers = buildHeaders(transactionId);

        HttpEntity<AccountGameCreateRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<GenericResponse<Long>> response = restTemplate.exchange(String.format("%s/api/account"
                    + "/create", host), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [createAccountGame] Client/Server Error: {}.  HTTP Status: {},  " +
                    "Body: {}", e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [createAccountGame] Unexpected Error: {} - TransactionId{}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public CharactersResponse characters(String host, String jwt, Long accountId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/characters", host))
                .queryParam(PARAM_ACCOUNT_ID, accountId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<CharactersResponse>> response = restTemplate.exchange(url, HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [characters] Client/Server Error: {}. The request failed with a client " +
                    "or realm error. HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [characters] Unexpected Error: {}. An unexpected error occurred during " +
                    "the transaction with ID: {}.", e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public AccountDetailResponse account(String host, String jwt, Long accountId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/account/%s", host, accountId))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<AccountDetailResponse>> response = restTemplate.exchange(url, HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [account]  Client/Server Error: {}. The request failed with a client or " +
                    "realm error. HTTP Status: {}, Response Body: {}", e.getMessage(),
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [account] Unexpected Error: {}. An unexpected error occurred during the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public MailsResponse mails(String host, String jwt, Long characterId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/mails/%s", host, characterId))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<MailsResponse>> response = restTemplate.exchange(url, HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [mails] Client/Server Error: {}. The request failed with a client or " +
                    "realm error. HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("Unexpected Error: {}. An unexpected error occurred during the transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void deleteFriend(String host, String jwt, Long characterId, Long friendId, Long accountId, Long userId,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/social/%s/%s", host, characterId,
                friendId))
                .queryParam("account_id", accountId)
                .queryParam("user_id", userId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.DELETE,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [deleteFriend] Client/Server Error: {}. The request failed with a client" +
                    " or realm error. HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [deleteFriend] Unexpected Error: {}. An unexpected error occurred during" +
                    " the transaction with ID: {}.", e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public CharacterSocialResponse friends(String host, String jwt, Long characterId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/social/%s/friends", host, characterId))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<CharacterSocialResponse>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [friends]  Client/Server Error: {}. The request failed with a client or " +
                    "realm error. HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [friends] Unexpected Error: {}. An unexpected error occurred during the " +
                    "transaction with ID: {}.", e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void changePasswordGame(String host, String jwt, Long accountId, Long userId, String password, byte[] salt,
            Integer expansionId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<ChangePasswordRequest> entity = new HttpEntity<>(new ChangePasswordRequest(password, accountId,
                userId, expansionId,
                salt), headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/account/change-password", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [changePasswordGame] Client/Server Error: {}. The request failed with a " +
                    "client or realm " +
                    "error. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [changePasswordGame]  Unexpected Error: {}. An unexpected error occurred" +
                    " during the transaction with ID: {}.", e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public List<CharacterProfessionsResponse> professions(String host, String jwt, Long accountId, Long characterId,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/professions", host))
                .queryParam(PARAM_ACCOUNT_ID, accountId)
                .queryParam("character_id", characterId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<List<CharacterProfessionsResponse>>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [professions] Client/Server Error: {}.  HTTP Status: {}, Response Body: " +
                    "{}", e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("Unexpected Error: {}. An unexpected error occurred during the transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void sendLevel(String host, String jwt, SendLevelRequest sendLevelRequest,
            String transactionId) {

        HttpHeaders headers = buildHeaders(transactionId, jwt);
        HttpEntity<SendLevelRequest> entity = new HttpEntity<>(sendLevelRequest, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/social/send/level", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [sendLevel] Message [{}] StatusCode [{}]  Body [{}]",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [sendLevel] Message [{}]  TransactionId [{}]", e.getMessage(),
                    transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }
        throw new InternalException("[IntegratorClient] [sendLevel] Unexpected transaction failure", transactionId);

    }

    public GenericResponse<Void> sendMoney(String host, String jwt, SendMoneyRequest sendMoneyRequest,
            String transactionId) {

        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<SendMoneyRequest> entity = new HttpEntity<>(sendMoneyRequest, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/social/send/money", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [sendMoney] Message [{}] StatusCode [{}]  Body [{}]",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [sendLevel] Message [{}]  TransactionId [{}]", e.getMessage(),
                    transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("[IntegratorClient] [sendMoney] Unexpected transaction failure", transactionId);

    }

    public CharactersResponse loanApplicationCharacters(String host, String jwt, Long accountId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/characters/loan/bank", host))
                .queryParam(PARAM_ACCOUNT_ID, accountId)
                .queryParam("time", 7200) // 2horas
                .queryParam("level", 80)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<CharactersResponse>> response = restTemplate.exchange(url, HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("Client/Server Error: {}. The request failed with a client or realm error. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("Unexpected Error: {}. An unexpected error occurred during the transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public GuildsResponse guilds(String host, String jwt, int size, int page, String search, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/guilds", host))
                .queryParam("size", size)
                .queryParam("page", page)
                .queryParam("search", search)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<GuildsResponse>> response = restTemplate.exchange(url, HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [guilds] Client/Server Error: {}. Error with realm client getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [guilds] Unexpected Error: {}. An unexpected error occurred during the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public GuildResponse guild(String host, String jwt, Long guid, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/guilds/%s", host, guid))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<GuildResponse>> response = restTemplate.exchange(url, HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [guild] Client/Server Error: {}. Error with realm client getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [guild] Unexpected Error: {}. An unexpected error occurred during the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void attachGuild(String host, String jwt, Long guildId, Long accountId,
            Long characterId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/guilds/attach", host))
                .queryParam("account_id", accountId)
                .queryParam("guild_id", guildId)
                .queryParam("character_id", characterId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<GuildResponse>> response = restTemplate.exchange(url, HttpMethod.PUT,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [attachGuild] Client/Server Error: {}. Error with realm client getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [attachGuild] Unexpected Error: {}. An unexpected error occurred during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public GuildDetailMemberResponse guildMember(String host, String jwt, Long userId, Long accountId, Long characterId,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/guilds/member", host))
                .queryParam("account_id", accountId)
                .queryParam("character_id", characterId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<GuildDetailMemberResponse>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [guildMember] Client/Server Error: {}. Error with realm client getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [guildMember] Unexpected Error: {}. An unexpected error occurred during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void unInviteGuild(String host, String jwt, Long userId, Long accountId,
            Long characterId, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/guilds/member", host))
                .queryParam("user_id", userId)
                .queryParam("account_id", accountId)
                .queryParam("character_id", characterId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<GuildResponse>> response = restTemplate.exchange(url, HttpMethod.DELETE,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [unInviteGuild] Client/Server Error: {}. Error with realm client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [unInviteGuild] Unexpected Error: {}. An unexpected error occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void sendCommand(String host, String jwt, String message, byte[] salt,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);
        ExecuteCommandRequest request = new ExecuteCommandRequest(message, salt);
        HttpEntity<ExecuteCommandRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/commands", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [sendCommand]  Client/Server Error: {}. Error with realm client " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [sendCommand] Unexpected Error: {}. An unexpected error occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public Double collectGold(String host, String jwt, Long userId, Double moneyToPay,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);
        BankCollectGoldRequest request = new BankCollectGoldRequest(userId, moneyToPay);
        HttpEntity<BankCollectGoldRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/bank/payment", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Double>> response = restTemplate.exchange(url, HttpMethod.PUT,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [collectGold]  Client/Server Error: {}. Error with realm client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            return moneyToPay;
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [collectGold] Unexpected Error: {}. An unexpected error occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            return moneyToPay;
        }

        return moneyToPay;
    }

    public void purchase(String host, String jwt, Long userId, Long accountId, String reference,
            List<ItemQuantityModel> items, Double amount, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        CreateTransactionItemsRequest request = new CreateTransactionItemsRequest(userId, accountId, reference, items,
                amount);
        HttpEntity<CreateTransactionItemsRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/transaction/purchase", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [createTransactionItems]  Client/Server Error: {}. Error with realm " +
                    "client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [createTransactionItems] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void updateGuild(String host, String jwt, Long characterId, Long accountId,
            boolean isPublic,
            boolean multiFaction, String discord, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        UpdateGuildRequest request = new UpdateGuildRequest(discord, isPublic, multiFaction, accountId, characterId);
        HttpEntity<UpdateGuildRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/guilds/edit", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.PUT,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [updateGuild]  Client/Server Error: {}. Error with realm " +
                    "client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [updateGuild] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void sendAnnouncement(String host, String jwt, AnnouncementRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<AnnouncementRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/professions/announcement", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [sendAnnouncement]  Client/Server Error: {}. Error with realm " +
                    "client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [sendAnnouncement] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void sendBenefitsPremium(String host, String jwt, SubscriptionBenefitsRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<SubscriptionBenefitsRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/transaction/subscription-benefits", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [sendBenefitsPremium]  Client/Server Error: {}. Error with realm " +
                    "client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [sendBenefitsPremium] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void sendPromo(String host, String jwt, ClaimPromoRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<ClaimPromoRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/transaction/claim-promotions", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [sendPromo]  Client/Server Error: {}. Error with realm " +
                    "client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [sendPromo] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void sendGuildBenefits(String host, String jwt, BenefitsGuildRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<BenefitsGuildRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/transaction/claim-guild-benefits", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [sendGuildBenefits]  Client/Server Error: {}. Error with realm " +
                    "client " +
                    "getting " +
                    "associated guilds. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [sendGuildBenefits] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public GenericResponse<ClaimMachineResponse> claimMachine(String host, String jwt, ClaimMachineRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<ClaimMachineRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/transaction/claim-machine", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<ClaimMachineResponse>> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [claimMachine]  Client/Server Error: {}. It was not possible to claim " +
                    "the shipment of the prize to the client because he won at the slot machine " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            return new GenericResponseBuilder<>(new ClaimMachineResponse(false), transactionId).build();
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [claimMachine] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            return new GenericResponseBuilder<>(new ClaimMachineResponse(false), transactionId).build();
        }

        return new GenericResponseBuilder<>(new ClaimMachineResponse(false), transactionId).build();
    }

    public GenericResponse<AccountsResponse> accountsServer(String host, String jwt, int size, int page,
            String filter,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/account/all", host))
                .queryParam("size", size)
                .queryParam("page", page)
                .queryParam("filter", filter)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<AccountsResponse>> response = restTemplate.exchange(url, HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [accountsServer]  Client/Server Error: {}. Could not get realm accounts" +
                    "  " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [accountsServer] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public GenericResponse<DashboardMetricsResponse> metricsDashboard(String host, String jwt, String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/dashboard/stats", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<DashboardMetricsResponse>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [accountsServer]  Client/Server Error: {}. Could not get realm accounts" +
                    " HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [accountsServer] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void updateMail(String host, String jwt,
            AccountUpdateMailRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<AccountUpdateMailRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/dashboard/account/email", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.PUT,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [updateMail]  Client/Server Error: {}. Could not get realm accounts" +
                    "  " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [updateMail] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public GenericResponse<List<CharacterInventoryResponse>> getCharacterInventory(String host, String jwt,
            Long characterId,
            Long accountId,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/characters/%d/inventory", host,
                characterId))
                .queryParam(PARAM_ACCOUNT_ID, accountId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<List<CharacterInventoryResponse>>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [getCharacterInventory] Error: {}. Status: {} Response Body:  {} " +
                    "transactionId {} ",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString(), transactionId);
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [getCharacterInventory] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void transferInventoryItem(String host, String jwt, TransferInventoryRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<TransferInventoryRequest> entity = new HttpEntity<>(request, headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/characters/inventory/transfer", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [transferInventoryItem]  Client/Server Error: {}. Could not get realm " +
                    "accounts" +
                    "  " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [transferInventoryItem] Unexpected Error: {}. An unexpected error " +
                    "occurred " +
                    "during " +
                    "the " +
                    "transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void banAccount(String host, String jwt, AccountBanRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<AccountBanRequest> entity = new HttpEntity<>(request, headers);
        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/dashboard/account/ban", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [banAccount] Error: {}. Status: {} Response Body:  {} transactionId {} ",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString(), transactionId);
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [banAccount]  Error: {}. TransactionId: {}.", e.getMessage(),
                    transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public GenericResponse<Map<String, String>> emulatorConfiguration(String host, String jwt,
            EmulatorConfigRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<EmulatorConfigRequest> entity = new HttpEntity<>(request, headers);
        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/dashboard/emulator-config", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Map<String, String>>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [emulatorConfiguration] Error: {}. Status: {} Response Body:  {} " +
                    "transactionId {} ",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString(), transactionId);
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [emulatorConfiguration]  Error: {}. TransactionId: {}.", e.getMessage(),
                    transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void teleport(String host, String jwt, TeleportRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<TeleportRequest> entity = new HttpEntity<>(request, headers);
        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/characters/teleport", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [Teleport] Message: {}. Status: {} Response:  {} " +
                    "TransactionId {} ",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString(), transactionId);
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [Teleport]  Error: {}. TransactionId: {}.", e.getMessage(),
                    transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public void deductTokens(String host, String jwt, DeductTokensDto request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<DeductTokensDto> entity = new HttpEntity<>(request, headers);
        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/transaction/deduct-tokens", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Void>> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [deductTokens] Message: {}. Status: {} Response:  {} " +
                    "TransactionId {} ",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString(), transactionId);
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [deductTokens]  Error: {}. TransactionId: {}.", e.getMessage(),
                    transactionId, e);
            throw new InternalException("Transaction failed due to client or realm error", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public CharacterDetailDto getCharacter(String host, String jwt, Long characterId, Long accountId,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/characters/%s", host, characterId))
                .queryParam(PARAM_ACCOUNT_ID, accountId)
                .toUriString();

        try {
            ResponseEntity<GenericResponse<CharacterDetailDto>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [getCharacter] Client/Server Error: {}. Error getting character details. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [getCharacter] Unexpected Error: {}. An unexpected error occurred " +
                    "during the transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }

    public Boolean updateStats(String host, String jwt, UpdateStatsRequest request,
            String transactionId) {
        HttpHeaders headers = buildHeaders(transactionId, jwt);

        HttpEntity<UpdateStatsRequest> entity = new HttpEntity<>(request, headers);
        String url = UriComponentsBuilder.fromUriString(String.format("%s/api/characters/stats", host))
                .toUriString();

        try {
            ResponseEntity<GenericResponse<Boolean>> response = restTemplate.exchange(url,
                    HttpMethod.PUT,
                    entity, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("[IntegratorClient] [updateStats] Client/Server Error: {}. Error updating character stats. " +
                    "HTTP Status: {}, Response Body: {}",
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalException(Objects.requireNonNull(e.getResponseBodyAs(GenericResponse.class)).getMessage(),
                    transactionId);
        } catch (Exception e) {
            LOGGER.error("[IntegratorClient] [updateStats] Unexpected Error: {}. An unexpected error occurred " +
                    "during the transaction with ID: {}.",
                    e.getMessage(), transactionId, e);
            throw new InternalException("Unexpected transaction failure", transactionId);
        }

        throw new InternalException("Unexpected transaction failure", transactionId);
    }
}
