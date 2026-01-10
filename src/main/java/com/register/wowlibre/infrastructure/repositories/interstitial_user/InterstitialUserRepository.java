package com.register.wowlibre.infrastructure.repositories.interstitial_user;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface InterstitialUserRepository extends JpaRepository<InterstitialUserEntity, Integer> {
    List<InterstitialUserEntity> findByUserId(Long userId);
}
