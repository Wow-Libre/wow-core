package com.register.wowlibre.infrastructure.repositories.interstitial_user;

import com.register.wowlibre.domain.port.out.interstitial_user.ObtainInterstitialUser;
import com.register.wowlibre.domain.port.out.interstitial_user.SaveInterstitialUser;
import com.register.wowlibre.infrastructure.entities.InterstitialUserEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JpaInterstitialUserAdapter implements SaveInterstitialUser, ObtainInterstitialUser {
    private final InterstitialUserRepository interstitialUserJpaRepository;

    public JpaInterstitialUserAdapter(InterstitialUserRepository interstitialUserJpaRepository) {
        this.interstitialUserJpaRepository = interstitialUserJpaRepository;
    }

    @Override
    public void saveInterstitialUser(InterstitialUserEntity interstitialUserEntity, String transactionId) {
        interstitialUserJpaRepository.save(interstitialUserEntity);
    }

    @Override
    public List<InterstitialUserEntity> findByUserId(Long userId, String transactionId) {
        return interstitialUserJpaRepository.findByUserId(userId);
    }

    @Override
    public Map<Long, long[]> getViewStatsPerInterstitial(String transactionId) {
        List<Object[]> rows = interstitialUserJpaRepository.findViewStatsGroupByInterstitialId();
        Map<Long, long[]> map = new HashMap<>();
        for (Object[] row : rows) {
            Long interstitialId = (Long) row[0];
            long totalViews = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
            long uniqueViewers = row[2] instanceof Number ? ((Number) row[2]).longValue() : 0L;
            map.put(interstitialId, new long[] { totalViews, uniqueViewers });
        }
        return map;
    }
}
