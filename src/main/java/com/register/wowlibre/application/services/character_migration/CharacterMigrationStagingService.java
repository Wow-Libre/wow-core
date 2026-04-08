package com.register.wowlibre.application.services.character_migration;

import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.application.util.*;
import com.register.wowlibre.domain.dto.character_migration.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.character_migration.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.realm.*;
import com.register.wowlibre.domain.port.in.subscriptions.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.out.account_game.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.character_migration.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.nio.charset.*;
import java.time.*;
import java.util.*;

@Service
@Slf4j
public class CharacterMigrationStagingService implements CharacterMigrationStagingPort {

    private static final int TARGET_GAME_USERNAME_MIN_LEN = 5;
    private static final int TARGET_GAME_USERNAME_MAX_LEN = 20;

    /**
     * Max completed character migrations per user when there is no active subscription.
     */
    private static final int MAX_COMPLETED_MIGRATIONS_WITHOUT_SUBSCRIPTION = 1;

    private final CharacterMigrationStagingRepository repository;
    private final CharacterMigrationAllowedSourceRepository allowedSourceRepository;
    private final RealmPort realmPort;
    private final IntegratorPort integratorPort;
    private final ObjectMapper objectMapper;
    private final AccountGamePort accountGamePort;
    private final ObtainAccountGamePort obtainAccountGamePort;
    private final UserPort userPort;
    private final SubscriptionPort subscriptionPort;

    public CharacterMigrationStagingService(CharacterMigrationStagingRepository repository,
                                            CharacterMigrationAllowedSourceRepository allowedSourceRepository,
                                            RealmPort realmPort,
                                            IntegratorPort integratorPort,
                                            ObjectMapper objectMapper,
                                            AccountGamePort accountGamePort,
                                            ObtainAccountGamePort obtainAccountGamePort,
                                            UserPort userPort,
                                            SubscriptionPort subscriptionPort) {
        this.repository = repository;
        this.allowedSourceRepository = allowedSourceRepository;
        this.realmPort = realmPort;
        this.integratorPort = integratorPort;
        this.objectMapper = objectMapper;
        this.accountGamePort = accountGamePort;
        this.obtainAccountGamePort = obtainAccountGamePort;
        this.userPort = userPort;
        this.subscriptionPort = subscriptionPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterMigrationAllowedSourceOptionDto> listActiveAllowedSources(String tx) {
        return allowedSourceRepository.findAllByActiveTrueOrderByRealmlistHostAsc().stream()
                .map(e -> CharacterMigrationAllowedSourceOptionDto.builder()
                        .id(e.getId())
                        .realmlistHost(e.getRealmlistHost())
                        .displayName(e.getDisplayName() != null && !e.getDisplayName().isBlank()
                                ? e.getDisplayName()
                                : e.getRealmlistHost())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public CharacterMigrationStagingDetailDto uploadFromFile(
            Long userId, Long realmId, Long allowedSourceId, byte[] fileBytes,
            CharacterMigrationTargetAccountMode targetAccountMode,
            String targetGameAccountUsername,
            Long targetExistingAccountId,
            String transactionId) {

        if (fileBytes == null || fileBytes.length == 0) {
            throw new BadRequestException("The migration file is required", transactionId);
        }

        assertUserMaySubmitMigration(userId, transactionId);

        CharacterMigrationTargetAccountMode mode = targetAccountMode != null
                ? targetAccountMode
                : CharacterMigrationTargetAccountMode.CREATE_NEW;

        RealmEntity realm = realmPort.findById(realmId, transactionId)
                .orElseThrow(() -> new InternalException("The kingdom to migrate to is not available " + realmId,
                        transactionId));

        String resolvedGameUsername;
        Long resolvedExistingAccountId = null;
        if (mode == CharacterMigrationTargetAccountMode.USE_EXISTING) {
            if (targetExistingAccountId == null) {
                throw new BadRequestException(
                        "When using an existing game account, target_existing_account_id is required.", transactionId);
            }
            AccountGameEntity owned = obtainAccountGamePort
                    .findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, targetExistingAccountId, realmId,
                            transactionId)
                    .orElseThrow(() -> new BadRequestException(
                            "The selected game account was not found, is inactive, or does not belong to you on this "
                                    + "realm.",
                            transactionId));
            resolvedGameUsername = owned.getUsername();
            resolvedExistingAccountId = owned.getAccountId();
        } else {
            resolvedGameUsername = validateAndNormalizeTargetGameAccountUsername(targetGameAccountUsername);
        }

        String text = new String(fileBytes, StandardCharsets.UTF_8);

        String jsonString;
        try {
            jsonString = ChdmpPayloadParser.extractJsonString(text);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), "");
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new BadRequestException("The decoded content is not valid JSON: " + e.getMessage(), "");
        }

        validateSourceRealmlist(root, allowedSourceId, transactionId);

        String canonicalJson;
        try {
            canonicalJson = objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new BadRequestException("Could not serialize the JSON", "");
        }

        String characterName = null;
        String characterGuid = null;
        JsonNode uinf = root.get("uinf");
        if (uinf != null && uinf.isObject()) {
            JsonNode name = uinf.get("name");
            JsonNode guid = uinf.get("guid");
            if (name != null && !name.isNull()) {
                characterName = name.asText();
            }
            if (guid != null && !guid.isNull()) {
                characterGuid = guid.asText();
            }
        }

        CharacterMigrationStagingEntity entity = new CharacterMigrationStagingEntity();
        entity.setUserId(userId);
        entity.setRealm(realm);
        entity.setCharacterName(characterName);
        entity.setCharacterGuid(characterGuid);
        entity.setTargetAccountMode(mode);
        entity.setTargetExistingAccountId(resolvedExistingAccountId);
        entity.setTargetGameAccountUsername(resolvedGameUsername);
        entity.setRawData(canonicalJson);
        entity.setStatus(CharacterMigrationStagingStatus.PENDING);
        entity.setValidationErrors(null);

        CharacterMigrationStagingEntity saved = repository.save(entity);
        return toDetailDto(saved, root);
    }

    /**
     * Enforces one pending migration per user and a cap on completed migrations without subscription.
     */
    private void assertUserMaySubmitMigration(Long userId, String transactionId) {
        if (repository.existsByUserIdAndStatus(userId, CharacterMigrationStagingStatus.PENDING)) {
            throw new BadRequestException(
                    "You already have a character migration request pending; wait until it is processed.",
                    transactionId);
        }
        if (!subscriptionPort.isActiveSubscription(userId, transactionId)) {
            long completed = repository.countByUserIdAndStatus(userId, CharacterMigrationStagingStatus.COMPLETED);
            if (completed >= MAX_COMPLETED_MIGRATIONS_WITHOUT_SUBSCRIPTION) {
                throw new BadRequestException(
                        "Without an active subscription you may complete at most "
                                + MAX_COMPLETED_MIGRATIONS_WITHOUT_SUBSCRIPTION + " character migrations.",
                        transactionId);
            }
        }
    }

    private String validateAndNormalizeTargetGameAccountUsername(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BadRequestException(
                    "Provide the game account username you want on the realm (between "
                            + TARGET_GAME_USERNAME_MIN_LEN + " and " + TARGET_GAME_USERNAME_MAX_LEN + " characters).",
                    "");
        }
        String s = raw.strip();
        if (s.length() < TARGET_GAME_USERNAME_MIN_LEN || s.length() > TARGET_GAME_USERNAME_MAX_LEN) {
            throw new BadRequestException(
                    "The game account username must be between " + TARGET_GAME_USERNAME_MIN_LEN
                            + " and " + TARGET_GAME_USERNAME_MAX_LEN + " characters.",
                    "");
        }
        return s;
    }

    /**
     * When active sources exist: requires {@code allowedSourceId}, that the row
     * exists, and that
     * {@code ginf.realmlist} matches that source (case-insensitive).
     */
    private void validateSourceRealmlist(JsonNode root, Long allowedSourceId, String tx) {
        long allowedCount = allowedSourceRepository.countByActiveTrue();
        if (allowedCount == 0) {
            log.warn("[{}] No rows in character_migration_allowed_source: skipping ginf.realmlist validation",
                    tx);
            return;
        }
        if (allowedSourceId == null) {
            throw new BadRequestException(
                    "You must specify the migration source server (allowed_source_id).", "");
        }
        CharacterMigrationAllowedSourceEntity chosen = allowedSourceRepository
                .findByIdAndActiveTrue(allowedSourceId)
                .orElseThrow(() -> new BadRequestException(
                        "The selected source server does not exist or is not available.", ""));

        JsonNode ginf = root.get("ginf");
        if (ginf == null || !ginf.isObject()) {
            throw new BadRequestException(
                    "The dump does not include the ginf block required to validate the migration source.", "");
        }
        JsonNode realmlistNode = ginf.get("realmlist");
        if (realmlistNode == null || realmlistNode.isNull() || realmlistNode.asText("").isBlank()) {
            throw new BadRequestException(
                    "The dump does not include ginf.realmlist; the source server cannot be validated.", "");
        }
        String hostFromDump = realmlistNode.asText().strip();
        String expected = chosen.getRealmlistHost() != null ? chosen.getRealmlistHost().strip() : "";
        if (expected.isEmpty() || !hostFromDump.equalsIgnoreCase(expected)) {
            throw new BadRequestException(
                    "The dump does not match the selected source: ginf.realmlist is \"" + hostFromDump
                            + "\" but you selected source \"" + expected
                            + "\". Choose the server you are migrating from or generate the dump against that " +
                            "realmlist.",
                    "");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterMigrationStagingListDto> listByRealm(Long realmId, String tx) {
        realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Realm not found: " + realmId, ""));
        return repository.findByRealm_IdOrderByCreatedAtDesc(realmId).stream()
                .map(this::toListDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterMigrationStagingListDto> listForUser(Long userId, Long realmId, String tx) {
        List<CharacterMigrationStagingEntity> rows;
        if (realmId != null) {
            realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Realm not found: " + realmId, ""));
            rows = repository.findByRealmIdAndUserIdWithRealmOrderByCreatedAtDesc(realmId, userId);
        } else {
            rows = repository.findAllByUserIdWithRealmOrderByCreatedAtDesc(userId);
        }
        return rows.stream().map(this::toListDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterMigrationStagingDetailDto getDetail(Long id, Long realmId, String tx) {
        CharacterMigrationStagingEntity entity = repository.findByIdAndRealm_Id(id, realmId)
                .orElseThrow(() -> new BadRequestException("Record not found", ""));
        JsonNode root;
        try {
            root = objectMapper.readTree(entity.getRawData());
        } catch (Exception e) {
            log.warn("[{}] Invalid raw_data for id={}", tx, id, e);
            root = objectMapper.createObjectNode();
        }
        return toDetailDto(entity, root);
    }

    @Override
    @Transactional
    public CharacterMigrationStagingDetailDto updateStatus(Long id, Long realmId,
                                                           CharacterMigrationStagingStatus status, String tx) {
        RealmEntity realm = realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Realm not " +
                "found: " + realmId, ""));

        CharacterMigrationStagingEntity characterMigrationStaging = repository.findByIdAndRealm_Id(id, realmId)
                .orElseThrow(() -> new BadRequestException("Record not found", ""));
        characterMigrationStaging.setStatus(status);

        if (status == CharacterMigrationStagingStatus.COMPLETED) {
            CharacterMigrationTargetAccountMode targetMode = characterMigrationStaging.getTargetAccountMode() != null
                    ? characterMigrationStaging.getTargetAccountMode()
                    : CharacterMigrationTargetAccountMode.CREATE_NEW;
            Long accountIdForIntegrator;
            if (targetMode == CharacterMigrationTargetAccountMode.USE_EXISTING) {
                if (characterMigrationStaging.getTargetExistingAccountId() == null) {
                    throw new InternalException("Migration request is missing target_existing_account_id.", tx);
                }
                accountIdForIntegrator = obtainAccountGamePort
                        .findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(characterMigrationStaging.getUserId(),
                                characterMigrationStaging.getTargetExistingAccountId(), realmId, tx)
                        .map(AccountGameEntity::getAccountId)
                        .orElseThrow(() -> new InternalException(
                                "Target game account is no longer available for this migration.", tx));
            } else {
                UserEntity userRequest = userPort.findByUserId(characterMigrationStaging.getUserId(), tx)
                        .orElseThrow(() -> new InternalException(
                                "User not found: " + characterMigrationStaging.getUserId(), tx));
                AccountGameEntity accountGame = accountGamePort.create(characterMigrationStaging.getUserId(),
                        realm.getName(), realm.getExpansionId(),
                        characterMigrationStaging.getTargetGameAccountUsername(), userRequest.getEmail(),
                        characterMigrationStaging.getTargetGameAccountUsername() + "wowlibre", tx);
                accountIdForIntegrator = accountGame.getAccountId();
            }

            WowLibreClientMigrationApprovedPayloadDto payload = buildMigrationApprovedPayload(characterMigrationStaging,
                    accountIdForIntegrator, realmId, realm.getEmulator());
            integratorPort.notifyCharacterMigrationApproved(realm.getHost(), realm.getJwt(), payload, tx);
        }

        CharacterMigrationStagingEntity saved = repository.save(characterMigrationStaging);

        JsonNode root;
        try {
            root = objectMapper.readTree(saved.getRawData());
        } catch (Exception e) {
            root = objectMapper.createObjectNode();
        }
        return toDetailDto(saved, root);
    }

    private WowLibreClientMigrationApprovedPayloadDto buildMigrationApprovedPayload(
            CharacterMigrationStagingEntity saved,
            Long accountId, Long realmId,
            String emulator) {
        CharacterMigrationDumpDto dump;
        try {
            dump = objectMapper.readValue(saved.getRawData(), CharacterMigrationDumpDto.class);
        } catch (Exception e) {
            log.warn("Invalid raw_data for integrator payload id={}", saved.getId(), e);
            dump = CharacterMigrationDumpDto.builder().build();
        }
        if (dump == null) {
            dump = CharacterMigrationDumpDto.builder().build();
        }
        String approvedAt = (saved.getUpdatedAt() != null
                ? saved.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()
                : Instant.now()).toString();
        return WowLibreClientMigrationApprovedPayloadDto.builder()
                .schemaVersion(WowLibreClientMigrationApprovedPayloadDto.SCHEMA_VERSION)
                .migrationId(saved.getId())
                .realmId(realmId)
                .accountId(accountId)
                .userId(saved.getUserId())
                .characterName(saved.getCharacterName())
                .characterGuid(saved.getCharacterGuid())
                .emulator(emulator)
                .status("COMPLETED")
                .approvedAt(approvedAt)
                .dump(dump)
                .build();
    }

    private CharacterMigrationStagingListDto toListDto(CharacterMigrationStagingEntity e) {
        return CharacterMigrationStagingListDto.builder()
                .id(e.getId())
                .realmId(e.getRealm() != null ? e.getRealm().getId() : null)
                .characterName(e.getCharacterName())
                .characterGuid(e.getCharacterGuid())
                .targetAccountMode(e.getTargetAccountMode() != null
                        ? e.getTargetAccountMode()
                        : CharacterMigrationTargetAccountMode.CREATE_NEW)
                .targetExistingAccountId(e.getTargetExistingAccountId())
                .targetGameAccountUsername(e.getTargetGameAccountUsername())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private CharacterMigrationStagingDetailDto toDetailDto(CharacterMigrationStagingEntity e, JsonNode rawNode) {
        return CharacterMigrationStagingDetailDto.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .realmId(e.getRealm() != null ? e.getRealm().getId() : null)
                .characterName(e.getCharacterName())
                .characterGuid(e.getCharacterGuid())
                .targetAccountMode(e.getTargetAccountMode() != null
                        ? e.getTargetAccountMode()
                        : CharacterMigrationTargetAccountMode.CREATE_NEW)
                .targetExistingAccountId(e.getTargetExistingAccountId())
                .targetGameAccountUsername(e.getTargetGameAccountUsername())
                .status(e.getStatus())
                .validationErrors(e.getValidationErrors())
                .rawData(rawNode)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
