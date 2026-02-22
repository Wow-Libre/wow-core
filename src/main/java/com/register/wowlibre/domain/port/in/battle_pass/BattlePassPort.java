package com.register.wowlibre.domain.port.in.battle_pass;

import com.register.wowlibre.domain.dto.battle_pass.*;

import java.util.List;

public interface BattlePassPort {

    BattlePassSeasonDto getActiveSeason(Long realmId, String transactionId);

    List<BattlePassRewardDto> getRewards(Long realmId, Long seasonId, String transactionId);

    BattlePassProgressDto getProgress(Long realmId, Long accountId, Long characterId, Long seasonId, Long userId, String transactionId);

    void claimReward(Long userId, BattlePassClaimRequestDto request, String transactionId);

    // Admin
    List<BattlePassSeasonDto> adminGetSeasons(Long realmId, String transactionId);

    BattlePassSeasonDto adminCreateSeason(BattlePassSeasonCreateDto dto, String transactionId);

    void adminUpdateSeason(Long seasonId, BattlePassSeasonCreateDto dto, String transactionId);

    List<BattlePassRewardDto> adminGetRewards(Long realmId, Long seasonId, String transactionId);

    BattlePassRewardDto adminCreateReward(BattlePassRewardCreateDto dto, String transactionId);

    void adminUpdateReward(Long rewardId, BattlePassRewardCreateDto dto, String transactionId);

    void adminDeleteReward(Long rewardId, String transactionId);
}
