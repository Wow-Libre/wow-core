package com.register.wowlibre.infrastructure.repositories.interstitial_user;

import com.register.wowlibre.domain.port.out.interstitial_user.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaInterstitialUserAdapter implements SaveInterstitialUser, ObtainInterstitialUser {
    private final InterstitialUserRepository interstitialUserJpaRepository;

    public JpaInterstitialUserAdapter(InterstitialUserRepository interstitialUserJpaRepository) {
        this.interstitialUserJpaRepository = interstitialUserJpaRepository;
    }


    @Override
    public void saveInterstitialUser(Long userId, InterstitialEntity interstitial, String transactionId) {
        InterstitialUserEntity interstitialUserEntity = new InterstitialUserEntity();
        interstitialUserEntity.setUserId(userId);
        interstitialUserEntity.setInterstitialId(interstitial);
        interstitialUserEntity.setViews(0L);
        interstitialUserJpaRepository.save(interstitialUserEntity);
    }

    @Override
    public List<InterstitialUserEntity> findByUserId(Long userId, String transactionId) {
        return interstitialUserJpaRepository.findByUserId(userId);
    }
}
