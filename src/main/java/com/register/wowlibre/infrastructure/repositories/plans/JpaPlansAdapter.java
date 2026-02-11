package com.register.wowlibre.infrastructure.repositories.plans;

import com.register.wowlibre.domain.port.out.plans.ManagePlan;
import com.register.wowlibre.domain.port.out.plans.ObtainPlan;
import com.register.wowlibre.infrastructure.entities.transactions.PlansEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaPlansAdapter implements ObtainPlan, ManagePlan {
    private final PlansRepository plansRepository;

    public JpaPlansAdapter(PlansRepository plansRepository) {
        this.plansRepository = plansRepository;
    }

    @Override
    public List<PlansEntity> findAll(String transactionId) {
        return (List<PlansEntity>) plansRepository.findAll();
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

    @Override
    public PlansEntity save(PlansEntity entity, String transactionId) {
        return plansRepository.save(entity);
    }

    @Override
    public void deleteById(Long id, String transactionId) {
        plansRepository.deleteById(id);
    }
}
