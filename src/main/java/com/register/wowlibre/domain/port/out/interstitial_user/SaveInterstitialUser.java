package com.register.wowlibre.domain.port.out.interstitial_user;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveInterstitialUser {
    void saveInterstitialUser(Long userId, InterstitialEntity interstitial, String transactionId);
}
