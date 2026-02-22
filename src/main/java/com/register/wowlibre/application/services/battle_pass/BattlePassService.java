package com.register.wowlibre.application.services.battle_pass;

import com.register.wowlibre.domain.dto.battle_pass.*;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.ItemQuantityModel;
import com.register.wowlibre.domain.port.in.account_validation.AccountValidationPort;
import com.register.wowlibre.domain.port.in.battle_pass.BattlePassPort;
import com.register.wowlibre.domain.port.in.characters.CharactersPort;
import com.register.wowlibre.domain.port.in.wowlibre.WowLibrePort;
import com.register.wowlibre.domain.port.out.battle_pass.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BattlePassService implements BattlePassPort {

    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String BENEFIT_TYPE_BATTLE_PASS = "BATTLE_PASS";

    private final ObtainBattlePassSeason obtainBattlePassSeason;
    private final SaveBattlePassSeason saveBattlePassSeason;
    private final ObtainBattlePassReward obtainBattlePassReward;
    private final SaveBattlePassReward saveBattlePassReward;
    private final DeleteBattlePassReward deleteBattlePassReward;
    private final ObtainBattlePassClaim obtainBattlePassClaim;
    private final SaveBattlePassClaim saveBattlePassClaim;
    private final CharactersPort charactersPort;
    private final AccountValidationPort accountValidationPort;
    private final WowLibrePort wowLibrePort;

    @Override
    public BattlePassSeasonDto getActiveSeason(Long realmId, String transactionId) {
        return obtainBattlePassSeason.findActiveByRealmId(realmId)
                .map(this::toSeasonDto)
                .orElse(null);
    }

    @Override
    public List<BattlePassRewardDto> getRewards(Long realmId, Long seasonId, String transactionId) {
        return obtainBattlePassReward.findBySeasonId(seasonId).stream()
                .map(this::toRewardDto)
                .collect(Collectors.toList());
    }

    @Override
    public BattlePassProgressDto getProgress(Long realmId, Long accountId, Long characterId, Long seasonId, Long userId, String transactionId) {
        accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId);
        var character = charactersPort.getCharacter(userId, characterId, accountId, transactionId);
        int characterLevel = character != null && character.getLevel() != null ? character.getLevel() : 0;
        var claims = obtainBattlePassClaim.findBySeasonAndCharacter(seasonId, realmId, accountId, characterId);
        List<Long> claimedIds = claims.stream().map(BattlePassClaimEntity::getRewardId).collect(Collectors.toList());
        return BattlePassProgressDto.builder()
                .characterLevel(characterLevel)
                .claimedRewardIds(claimedIds)
                .build();
    }

    @Override
    @Transactional
    public void claimReward(Long userId, BattlePassClaimRequestDto request, String transactionId) {
        accountValidationPort.verifyAccount(userId, request.getAccountId(), request.getRealmId(), transactionId);

        var seasonOpt = obtainBattlePassSeason.findById(request.getSeasonId());
        if (seasonOpt.isEmpty()) {
            throw new InternalException("Season not found", transactionId);
        }
        var season = seasonOpt.get();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(season.getStartDate()) || now.isAfter(season.getEndDate())) {
            throw new InternalException("Season is not active", transactionId);
        }

        var rewardOpt = obtainBattlePassReward.findById(request.getRewardId());
        if (rewardOpt.isEmpty()) {
            throw new InternalException("Reward not found", transactionId);
        }
        var reward = rewardOpt.get();
        if (!reward.getSeasonId().equals(request.getSeasonId())) {
            throw new InternalException("Reward does not belong to this season", transactionId);
        }

        if (obtainBattlePassClaim.existsClaim(request.getSeasonId(), request.getRealmId(), request.getAccountId(), request.getCharacterId(), request.getRewardId())) {
            throw new InternalException("Reward already claimed", transactionId);
        }

        var character = charactersPort.getCharacter(userId, request.getCharacterId(), request.getAccountId(), transactionId);
        int characterLevel = character != null && character.getLevel() != null ? character.getLevel() : 0;
        if (characterLevel < reward.getLevel()) {
            throw new InternalException("Character level " + characterLevel + " is below required level " + reward.getLevel(), transactionId);
        }

        var claim = BattlePassClaimEntity.builder()
                .seasonId(request.getSeasonId())
                .realmId(request.getRealmId())
                .accountId(request.getAccountId())
                .characterId(request.getCharacterId())
                .rewardId(request.getRewardId())
                .claimedAt(LocalDateTime.now())
                .build();
        saveBattlePassClaim.save(claim);

        List<ItemQuantityModel> items = List.of(new ItemQuantityModel(String.valueOf(reward.getCoreItemId()), 1));
        wowLibrePort.sendBenefitsPremium(request.getRealmId(), userId, request.getAccountId(),
                request.getCharacterId(), items, BENEFIT_TYPE_BATTLE_PASS, 0.0, transactionId);
    }

    @Override
    public List<BattlePassSeasonDto> adminGetSeasons(Long realmId, String transactionId) {
        return obtainBattlePassSeason.findAllByRealmId(realmId).stream()
                .map(this::toSeasonDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BattlePassSeasonDto adminCreateSeason(BattlePassSeasonCreateDto dto, String transactionId) {
        var entity = BattlePassSeasonEntity.builder()
                .realmId(dto.getRealmId())
                .name(dto.getName())
                .startDate(LocalDateTime.parse(dto.getStartDate(), ISO_DATE_TIME))
                .endDate(LocalDateTime.parse(dto.getEndDate(), ISO_DATE_TIME))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        var saved = saveBattlePassSeason.save(entity);
        return toSeasonDto(saved);
    }

    @Override
    @Transactional
    public void adminUpdateSeason(Long seasonId, BattlePassSeasonCreateDto dto, String transactionId) {
        var opt = obtainBattlePassSeason.findById(seasonId);
        if (opt.isEmpty()) {
            throw new InternalException("Season not found", transactionId);
        }
        var entity = opt.get();
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getStartDate() != null) entity.setStartDate(LocalDateTime.parse(dto.getStartDate(), ISO_DATE_TIME));
        if (dto.getEndDate() != null) entity.setEndDate(LocalDateTime.parse(dto.getEndDate(), ISO_DATE_TIME));
        entity.setUpdatedAt(LocalDateTime.now());
        saveBattlePassSeason.save(entity);
    }

    @Override
    public List<BattlePassRewardDto> adminGetRewards(Long realmId, Long seasonId, String transactionId) {
        return obtainBattlePassReward.findBySeasonId(seasonId).stream()
                .map(this::toRewardDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BattlePassRewardDto adminCreateReward(BattlePassRewardCreateDto dto, String transactionId) {
        var entity = BattlePassRewardEntity.builder()
                .seasonId(dto.getSeasonId())
                .level(dto.getLevel())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .coreItemId(dto.getCoreItemId())
                .wowheadId(dto.getWowheadId())
                .sortOrder(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        var saved = saveBattlePassReward.save(entity);
        return toRewardDto(saved);
    }

    @Override
    @Transactional
    public void adminUpdateReward(Long rewardId, BattlePassRewardCreateDto dto, String transactionId) {
        var opt = obtainBattlePassReward.findById(rewardId);
        if (opt.isEmpty()) {
            throw new InternalException("Reward not found", transactionId);
        }
        var entity = opt.get();
        if (dto.getLevel() != null) entity.setLevel(dto.getLevel());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getImageUrl() != null) entity.setImageUrl(dto.getImageUrl());
        if (dto.getCoreItemId() != null) entity.setCoreItemId(dto.getCoreItemId());
        if (dto.getWowheadId() != null) entity.setWowheadId(dto.getWowheadId());
        entity.setUpdatedAt(LocalDateTime.now());
        saveBattlePassReward.save(entity);
    }

    @Override
    @Transactional
    public void adminDeleteReward(Long rewardId, String transactionId) {
        if (obtainBattlePassReward.findById(rewardId).isEmpty()) {
            throw new InternalException("Reward not found", transactionId);
        }
        deleteBattlePassReward.deleteById(rewardId);
    }

    private BattlePassSeasonDto toSeasonDto(BattlePassSeasonEntity e) {
        return BattlePassSeasonDto.builder()
                .id(e.getId())
                .realmId(e.getRealmId())
                .name(e.getName())
                .startDate(e.getStartDate() != null ? e.getStartDate().format(ISO_DATE_TIME) : null)
                .endDate(e.getEndDate() != null ? e.getEndDate().format(ISO_DATE_TIME) : null)
                .isActive(e.getIsActive())
                .build();
    }

    private BattlePassRewardDto toRewardDto(BattlePassRewardEntity e) {
        return BattlePassRewardDto.builder()
                .id(e.getId())
                .seasonId(e.getSeasonId())
                .level(e.getLevel())
                .name(e.getName())
                .imageUrl(e.getImageUrl())
                .coreItemId(e.getCoreItemId())
                .wowheadId(e.getWowheadId())
                .build();
    }
}
