package com.register.wowlibre.domain.port.out.interstitial_user;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainInterstitialUser {
    List<InterstitialUserEntity> findByUserId(Long userId, String transactionId);
}
