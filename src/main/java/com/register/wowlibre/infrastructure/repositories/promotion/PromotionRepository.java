package com.register.wowlibre.infrastructure.repositories.promotion;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface PromotionRepository extends CrudRepository<PromotionEntity, Long> {

    List<PromotionEntity> findByLanguageAndStatusIsTrue(String language);

    List<PromotionEntity> findByRealmIdAndStatusIsTrue(Long realmId);

    List<PromotionEntity> findByLanguageAndRealmIdAndStatusIsTrue(String language, Long realmId);

}
