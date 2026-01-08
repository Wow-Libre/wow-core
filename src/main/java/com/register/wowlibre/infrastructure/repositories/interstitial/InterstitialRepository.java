package com.register.wowlibre.infrastructure.repositories.interstitial;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface InterstitialRepository extends JpaRepository<InterstitialEntity, Long> {
    List<InterstitialEntity> findByStatusTrue();
}
