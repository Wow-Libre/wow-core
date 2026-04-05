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
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.character_migration.*;
import com.register.wowlibre.infrastructure.util.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.nio.charset.*;
import java.time.*;
import java.util.*;

@Service
@Slf4j
public class CharacterMigrationStagingService implements CharacterMigrationStagingPort {

    /**
     * Alineado con {@link com.register.wowlibre.domain.dto.account_game.CreateAccountGameDto#getUsername()}.
     */
    private static final int TARGET_GAME_USERNAME_MIN_LEN = 5;
    private static final int TARGET_GAME_USERNAME_MAX_LEN = 20;

    private final CharacterMigrationStagingRepository repository;
    private final CharacterMigrationAllowedSourceRepository allowedSourceRepository;
    private final RealmPort realmPort;
    private final IntegratorPort integratorPort;
    private final ObjectMapper objectMapper;
    private final AccountGamePort accountGamePort;
    private final UserPort userPort;
    private final RandomString migrationGameAccountPasswordGenerator;

    public CharacterMigrationStagingService(CharacterMigrationStagingRepository repository,
                                            CharacterMigrationAllowedSourceRepository allowedSourceRepository,
                                            RealmPort realmPort,
                                            IntegratorPort integratorPort,
                                            ObjectMapper objectMapper,
                                            AccountGamePort accountGamePort,
                                            UserPort userPort,
                                            @Qualifier("resetPasswordString")
                                            RandomString migrationGameAccountPasswordGenerator) {
        this.repository = repository;
        this.allowedSourceRepository = allowedSourceRepository;
        this.realmPort = realmPort;
        this.integratorPort = integratorPort;
        this.objectMapper = objectMapper;
        this.accountGamePort = accountGamePort;
        this.userPort = userPort;
        this.migrationGameAccountPasswordGenerator = migrationGameAccountPasswordGenerator;
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
            Long adminUserId, Long realmId, Long allowedSourceId, byte[] fileBytes,
            String targetGameAccountUsername, String tx) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new BadRequestException("Archivo requerido", "");
        }
        String gameUsername = validateAndNormalizeTargetGameAccountUsername(targetGameAccountUsername);
        RealmEntity realm = realmPort.findById(realmId, tx)
                .orElseThrow(() -> new InternalException("Reino no encontrado: " + realmId, ""));

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
            throw new BadRequestException("El contenido decodificado no es JSON válido: " + e.getMessage(), "");
        }

        validateSourceRealmlist(root, allowedSourceId, tx);

        String canonicalJson;
        try {
            canonicalJson = objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new BadRequestException("No se pudo serializar el JSON", "");
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
        entity.setUserId(adminUserId);
        entity.setRealm(realm);
        entity.setCharacterName(characterName);
        entity.setCharacterGuid(characterGuid);
        entity.setTargetGameAccountUsername(gameUsername);
        entity.setRawData(canonicalJson);
        entity.setStatus(CharacterMigrationStagingStatus.PENDING);
        entity.setValidationErrors(null);

        CharacterMigrationStagingEntity saved = repository.save(entity);
        log.info("[{}] Migración staging creada id={} realmId={}", tx, saved.getId(), realmId);
        return toDetailDto(saved, root);
    }

    private String validateAndNormalizeTargetGameAccountUsername(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BadRequestException(
                    "Indicá el nombre de usuario de la cuenta de juego que querés en el reino (entre "
                            + TARGET_GAME_USERNAME_MIN_LEN + " y " + TARGET_GAME_USERNAME_MAX_LEN + " caracteres).",
                    "");
        }
        String s = raw.strip();
        if (s.length() < TARGET_GAME_USERNAME_MIN_LEN || s.length() > TARGET_GAME_USERNAME_MAX_LEN) {
            throw new BadRequestException(
                    "El nombre de usuario de la cuenta de juego debe tener entre " + TARGET_GAME_USERNAME_MIN_LEN
                            + " y " + TARGET_GAME_USERNAME_MAX_LEN + " caracteres.", "");
        }
        return s;
    }

    /**
     * Si hay orígenes activos: exige {@code allowedSourceId}, que el registro exista y que
     * {@code ginf.realmlist} coincida con ese origen (sin distinguir mayúsculas).
     */
    private void validateSourceRealmlist(JsonNode root, Long allowedSourceId, String tx) {
        long allowedCount = allowedSourceRepository.countByActiveTrue();
        if (allowedCount == 0) {
            log.warn("[{}] Sin orígenes en character_migration_allowed_source: se omite validación de ginf.realmlist",
                    tx);
            return;
        }
        if (allowedSourceId == null) {
            throw new BadRequestException(
                    "Debés indicar el servidor de origen de la migración (allowed_source_id).", "");
        }
        CharacterMigrationAllowedSourceEntity chosen = allowedSourceRepository
                .findByIdAndActiveTrue(allowedSourceId)
                .orElseThrow(() -> new BadRequestException(
                        "El servidor de origen seleccionado no existe o no está disponible.", ""));

        JsonNode ginf = root.get("ginf");
        if (ginf == null || !ginf.isObject()) {
            throw new BadRequestException(
                    "El dump no incluye el bloque ginf necesario para validar el origen de la migración.", "");
        }
        JsonNode realmlistNode = ginf.get("realmlist");
        if (realmlistNode == null || realmlistNode.isNull() || realmlistNode.asText("").isBlank()) {
            throw new BadRequestException(
                    "El dump no incluye ginf.realmlist; no se puede validar el servidor de origen.", "");
        }
        String hostFromDump = realmlistNode.asText().strip();
        String expected = chosen.getRealmlistHost() != null ? chosen.getRealmlistHost().strip() : "";
        if (expected.isEmpty() || !hostFromDump.equalsIgnoreCase(expected)) {
            throw new BadRequestException(
                    "El dump no coincide con el origen elegido: ginf.realmlist es \"" + hostFromDump
                            + "\" pero indicaste el origen \"" + expected
                            + "\". Elegí el servidor del que venís o generá el dump en ese realmlist.", "");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterMigrationStagingListDto> listByRealm(Long realmId, String tx) {
        realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Reino no encontrado: " + realmId, ""));
        return repository.findByRealm_IdOrderByCreatedAtDesc(realmId).stream()
                .map(this::toListDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterMigrationStagingListDto> listForUser(Long userId, Long realmId, String tx) {
        List<CharacterMigrationStagingEntity> rows;
        if (realmId != null) {
            realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Reino no encontrado: " + realmId
                    , ""));
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
                .orElseThrow(() -> new BadRequestException("Registro no encontrado", ""));
        JsonNode root;
        try {
            root = objectMapper.readTree(entity.getRawData());
        } catch (Exception e) {
            log.warn("[{}] raw_data inválido para id={}", tx, id, e);
            root = objectMapper.createObjectNode();
        }
        return toDetailDto(entity, root);
    }

    @Override
    @Transactional
    public CharacterMigrationStagingDetailDto updateStatus(Long id, Long realmId,
                                                           CharacterMigrationStagingStatus status, String tx) {
        RealmEntity realm = realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Reino no " +
                "encontrado: " + realmId, ""));

        CharacterMigrationStagingEntity characterMigrationStaging = repository.findByIdAndRealm_Id(id, realmId)
                .orElseThrow(() -> new BadRequestException("Registro no encontrado", ""));
        characterMigrationStaging.setStatus(status);
        CharacterMigrationStagingEntity saved = repository.save(characterMigrationStaging);


        if (status == CharacterMigrationStagingStatus.COMPLETED) {
            UserEntity userRequest =
                    userPort.findByUserId(characterMigrationStaging.getUserId(), tx).orElseThrow(() -> new InternalException(
                            "Usuario no  encontrado: " + characterMigrationStaging.getUserId(), tx));

            String generatedPassword = migrationGameAccountPasswordGenerator.nextString();
            AccountGameEntity accountGame = accountGamePort.create(characterMigrationStaging.getUserId(),
                    realm.getName(), realm.getExpansionId(),
                    characterMigrationStaging.getTargetGameAccountUsername(), userRequest.getEmail(),
                    "sebastian", tx);

            WowLibreClientMigrationApprovedPayloadDto payload = buildMigrationApprovedPayload(saved,
                    accountGame.getAccountId(), realmId, realm.getEmulator());
            integratorPort.notifyCharacterMigrationApproved(realm.getHost(), realm.getJwt(), payload, tx);
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(saved.getRawData());
        } catch (Exception e) {
            root = objectMapper.createObjectNode();
        }
        return toDetailDto(saved, root);
    }


    private WowLibreClientMigrationApprovedPayloadDto buildMigrationApprovedPayload(CharacterMigrationStagingEntity saved,
                                                                                    Long accountId, Long realmId,
                                                                                    String emulator) {
        CharacterMigrationDumpDto dump;
        try {
            dump = objectMapper.readValue(saved.getRawData(), CharacterMigrationDumpDto.class);
        } catch (Exception e) {
            log.warn("raw_data inválido para payload integrador id={}", saved.getId(), e);
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
                .targetGameAccountUsername(e.getTargetGameAccountUsername())
                .status(e.getStatus())
                .validationErrors(e.getValidationErrors())
                .rawData(rawNode)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
