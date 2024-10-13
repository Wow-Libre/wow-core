package com.register.wowlibre.infrastructure.repositories.benefit_guild;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface BenefitGuildRepository extends CrudRepository<BenefitGuildEntity, Long> {
    List<BenefitGuildEntity> findByServerId_idAndGuildIdAndStatusIsTrue(Long serverId, Long guildId);
}
