package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.character_migration.CharacterMigrationStatusUpdateDto;
import com.register.wowlibre.domain.dto.character_migration.CharacterMigrationStagingDetailDto;
import com.register.wowlibre.domain.dto.character_migration.CharacterMigrationStagingListDto;
import com.register.wowlibre.domain.exception.BadRequestException;
import com.register.wowlibre.domain.port.in.character_migration.CharacterMigrationStagingPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/api/character-migration")
public class CharacterMigrationStagingController {

    private final CharacterMigrationStagingPort characterMigrationStagingPort;

    public CharacterMigrationStagingController(CharacterMigrationStagingPort characterMigrationStagingPort) {
        this.characterMigrationStagingPort = characterMigrationStagingPort;
    }

    /**
     * Carga de dump por usuario autenticado (cuenta web). Misma lógica que admin/upload.
     */
    @PostMapping(value = "/me/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<CharacterMigrationStagingDetailDto>> userUpload(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestParam(name = "realm_id") Long realmId,
            @RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Archivo requerido (campo multipart 'file')", transactionId != null ? transactionId : "");
        }
        CharacterMigrationStagingDetailDto created = characterMigrationStagingPort.uploadFromFile(
                userId, realmId, file.getBytes(), transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    /**
     * Estado de las migraciones del usuario. {@code realm_id} opcional: si se omite, todas las solicitudes del usuario.
     */
    @GetMapping("/me/list")
    public ResponseEntity<GenericResponse<List<CharacterMigrationStagingListDto>>> userList(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestParam(name = "realm_id", required = false) Long realmId) {
        List<CharacterMigrationStagingListDto> list =
                characterMigrationStagingPort.listForUser(userId, realmId, transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(list, transactionId).ok().build());
    }

    @PostMapping(value = "/admin/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<CharacterMigrationStagingDetailDto>> adminUpload(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestParam(name = "realm_id") Long realmId,
            @RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Archivo requerido (campo multipart 'file')", transactionId != null ? transactionId : "");
        }
        CharacterMigrationStagingDetailDto created = characterMigrationStagingPort.uploadFromFile(
                userId, realmId, file.getBytes(), transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    @GetMapping("/admin/list")
    public ResponseEntity<GenericResponse<List<CharacterMigrationStagingListDto>>> adminList(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(name = "realm_id") Long realmId) {
        List<CharacterMigrationStagingListDto> list = characterMigrationStagingPort.listByRealm(realmId, transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(list, transactionId).ok().build());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<GenericResponse<CharacterMigrationStagingDetailDto>> adminDetail(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(name = "realm_id") Long realmId,
            @PathVariable Long id) {
        CharacterMigrationStagingDetailDto detail = characterMigrationStagingPort.getDetail(id, realmId, transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(detail, transactionId).ok().build());
    }

    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<GenericResponse<CharacterMigrationStagingDetailDto>> adminUpdateStatus(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(name = "realm_id") Long realmId,
            @PathVariable Long id,
            @Valid @RequestBody CharacterMigrationStatusUpdateDto body) {
        CharacterMigrationStagingDetailDto updated = characterMigrationStagingPort.updateStatus(
                id, realmId, body.getStatus(), transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(updated, transactionId).ok().build());
    }
}
