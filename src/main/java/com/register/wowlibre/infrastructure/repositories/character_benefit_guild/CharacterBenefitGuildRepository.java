package com.register.wowlibre.infrastructure.repositories.character_benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface CharacterBenefitGuildRepository extends CrudRepository<CharacterBenefitGuildEntity, Long> {

    List<CharacterBenefitGuildEntity> findByCharacterIdAndAccountId(Long characterId, Long accountId);

}
