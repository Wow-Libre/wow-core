package com.register.wowlibre.domain.port.out.interstitial_user;

import com.register.wowlibre.infrastructure.entities.InterstitialUserEntity;

public interface SaveInterstitialUser {
    void saveInterstitialUser(InterstitialUserEntity interstitialUserEntity, String transactionId);
}
