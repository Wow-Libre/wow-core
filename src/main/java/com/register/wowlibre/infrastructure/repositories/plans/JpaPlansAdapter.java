package com.register.wowlibre.infrastructure.repositories.plans;

import com.register.wowlibre.domain.port.out.plans.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaPlansAdapter implements ObtainPlan {
    private final PlansRepository plansRepository;

    public JpaPlansAdapter(PlansRepository plansRepository) {
        this.plansRepository = plansRepository;
    }

    @Override
    public List<PlansEntity> findByStatusIsTrue(String transactionId) {
        return plansRepository.findByStatusIsTrue();
    }

    @Override
    public List<PlansEntity> findByStatusIsTrueAndLanguage(String language, String transactionId) {
        return plansRepository.findByStatusIsTrueAndLanguage(language);
    }

    @Override
    public Optional<PlansEntity> findById(Long planId, String transactionId) {
        return plansRepository.findById(planId);
    }
}
