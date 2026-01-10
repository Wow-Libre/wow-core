package com.register.wowlibre.domain.port.out.interstitial;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainInterstitial {
    List<InterstitialEntity> findAllActiveInterstitials(String transactionId);

    Optional<InterstitialEntity> findById(Long id, String transactionId);
}
