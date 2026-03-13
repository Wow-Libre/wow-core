package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.battle_pass.*;
import com.register.wowlibre.domain.port.in.battle_pass.BattlePassPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;
import static com.register.wowlibre.domain.constant.Constants.PARAM_ACCOUNT_ID;
import static com.register.wowlibre.domain.constant.Constants.PARAM_CHARACTER_ID;
import static com.register.wowlibre.domain.constant.Constants.PARAM_REALM_ID;

@RestController
@RequestMapping("/api/battle-pass")
public class BattlePassController {

    private final BattlePassPort battlePassPort;

    public BattlePassController(BattlePassPort battlePassPort) {
        this.battlePassPort = battlePassPort;
    }

    @GetMapping("/season")
    public ResponseEntity<GenericResponse<BattlePassSeasonDto>> getActiveSeason(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(name = "realm_id") Long realmId) {
        BattlePassSeasonDto season = battlePassPort.getActiveSeason(realmId, transactionId);
        if (season == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(
                new GenericResponseBuilder<>(season, transactionId).ok().build());
    }

    @GetMapping("/rewards")
    public ResponseEntity<GenericResponse<List<BattlePassRewardDto>>> getRewards(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(name = "realm_id") Long realmId,
            @RequestParam(name = "season_id") Long seasonId) {
        List<BattlePassRewardDto> rewards = battlePassPort.getRewards(realmId, seasonId, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<>(rewards, transactionId).ok().build());
    }

    @GetMapping("/progress")
    public ResponseEntity<GenericResponse<BattlePassProgressDto>> getProgress(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestParam(name = "realm_id") Long realmId,
            @RequestParam(name = PARAM_ACCOUNT_ID) Long accountId,
            @RequestParam(name = PARAM_CHARACTER_ID) Long characterId,
            @RequestParam(name = "season_id") Long seasonId) {
        BattlePassProgressDto progress = battlePassPort.getProgress(realmId, accountId, characterId, seasonId, userId, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<>(progress, transactionId).ok().build());
    }

    @PostMapping("/claim")
    public ResponseEntity<GenericResponse<Void>> claim(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestBody @Valid BattlePassClaimRequestDto request) {
        battlePassPort.claimReward(userId, request, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    // --- Admin ---

    @GetMapping("/admin/seasons")
    public ResponseEntity<GenericResponse<List<BattlePassSeasonDto>>> adminGetSeasons(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(name = "realm_id") Long realmId) {
        List<BattlePassSeasonDto> seasons = battlePassPort.adminGetSeasons(realmId, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<>(seasons, transactionId).ok().build());
    }

    @PostMapping("/admin/seasons")
    public ResponseEntity<GenericResponse<BattlePassSeasonDto>> adminCreateSeason(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestBody @Valid BattlePassSeasonCreateDto dto) {
        BattlePassSeasonDto created = battlePassPort.adminCreateSeason(dto, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    @PutMapping("/admin/seasons/{id}")
    public ResponseEntity<GenericResponse<Void>> adminUpdateSeason(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @PathVariable Long id,
            @RequestBody @Valid BattlePassSeasonCreateDto dto) {
        battlePassPort.adminUpdateSeason(id, dto, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @GetMapping("/admin/rewards")
    public ResponseEntity<GenericResponse<List<BattlePassRewardDto>>> adminGetRewards(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(name = "realm_id") Long realmId,
            @RequestParam(name = "season_id") Long seasonId) {
        List<BattlePassRewardDto> rewards = battlePassPort.adminGetRewards(realmId, seasonId, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<>(rewards, transactionId).ok().build());
    }

    @PostMapping("/admin/rewards")
    public ResponseEntity<GenericResponse<BattlePassRewardDto>> adminCreateReward(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestBody @Valid BattlePassRewardCreateDto dto) {
        BattlePassRewardDto created = battlePassPort.adminCreateReward(dto, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    @PutMapping("/admin/rewards/{id}")
    public ResponseEntity<GenericResponse<Void>> adminUpdateReward(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @PathVariable Long id,
            @RequestBody @Valid BattlePassRewardCreateDto dto) {
        battlePassPort.adminUpdateReward(id, dto, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @DeleteMapping("/admin/rewards/{id}")
    public ResponseEntity<GenericResponse<Void>> adminDeleteReward(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @PathVariable Long id) {
        battlePassPort.adminDeleteReward(id, transactionId);
        return ResponseEntity.ok(
                new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
