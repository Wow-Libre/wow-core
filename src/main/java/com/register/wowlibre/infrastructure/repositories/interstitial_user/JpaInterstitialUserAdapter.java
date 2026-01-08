package com.register.wowlibre.infrastructure.repositories.interstitial_user;

import com.register.wowlibre.domain.port.out.interstitial_user.ObtainInterstitialUser;
import com.register.wowlibre.domain.port.out.interstitial_user.SaveInterstitialUser;
import com.register.wowlibre.infrastructure.entities.InterstitialEntity;
import com.register.wowlibre.infrastructure.entities.InterstitialUserEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
}
