package com.register.wowlibre.application.services.character_migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.register.wowlibre.application.util.ChdmpPayloadParser;
import com.register.wowlibre.domain.dto.character_migration.CharacterMigrationStagingDetailDto;
import com.register.wowlibre.domain.dto.character_migration.CharacterMigrationStagingListDto;
import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import com.register.wowlibre.domain.exception.BadRequestException;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.port.in.character_migration.CharacterMigrationStagingPort;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.infrastructure.entities.CharacterMigrationStagingEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.repositories.character_migration.CharacterMigrationStagingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterMigrationStagingService implements CharacterMigrationStagingPort {

    private final CharacterMigrationStagingRepository repository;
    private final RealmPort realmPort;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CharacterMigrationStagingDetailDto uploadFromFile(Long adminUserId, Long realmId, byte[] fileBytes, String tx) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new BadRequestException("Archivo requerido", "");
        }
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
        entity.setRawData(canonicalJson);
        entity.setStatus(CharacterMigrationStagingStatus.PENDING);
        entity.setValidationErrors(null);

        CharacterMigrationStagingEntity saved = repository.save(entity);
        log.info("[{}] Migración staging creada id={} realmId={}", tx, saved.getId(), realmId);
        return toDetailDto(saved, root);
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
            realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Reino no encontrado: " + realmId, ""));
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
    public CharacterMigrationStagingDetailDto updateStatus(Long id, Long realmId, CharacterMigrationStagingStatus status, String tx) {
        realmPort.findById(realmId, tx).orElseThrow(() -> new InternalException("Reino no encontrado: " + realmId, ""));
        CharacterMigrationStagingEntity entity = repository.findByIdAndRealm_Id(id, realmId)
                .orElseThrow(() -> new BadRequestException("Registro no encontrado", ""));
        entity.setStatus(status);
        CharacterMigrationStagingEntity saved = repository.save(entity);
        log.info("[{}] Migración staging id={} estado actualizado a {}", tx, id, status);
        JsonNode root;
        try {
            root = objectMapper.readTree(saved.getRawData());
        } catch (Exception e) {
            root = objectMapper.createObjectNode();
        }
        return toDetailDto(saved, root);
    }

    private CharacterMigrationStagingListDto toListDto(CharacterMigrationStagingEntity e) {
        return CharacterMigrationStagingListDto.builder()
                .id(e.getId())
                .realmId(e.getRealm() != null ? e.getRealm().getId() : null)
                .characterName(e.getCharacterName())
                .characterGuid(e.getCharacterGuid())
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
                .status(e.getStatus())
                .validationErrors(e.getValidationErrors())
                .rawData(rawNode)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
