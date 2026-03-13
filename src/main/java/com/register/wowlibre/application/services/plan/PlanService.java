package com.register.wowlibre.application.services.plan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.register.wowlibre.domain.dto.PlanAdminDto;
import com.register.wowlibre.domain.dto.PlanAdminRequestDto;
import com.register.wowlibre.domain.dto.PlanDetailDto;
import com.register.wowlibre.domain.port.in.plan.PlanPort;
import com.register.wowlibre.domain.port.out.plans.ManagePlan;
import com.register.wowlibre.domain.port.out.plans.ObtainPlan;
import com.register.wowlibre.infrastructure.entities.transactions.PlansEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlanService implements PlanPort {

    private final ObtainPlan obtainPlan;
    private final ManagePlan managePlan;
    private final ObjectMapper objectMapper;

    public PlanService(ObtainPlan obtainPlan, ManagePlan managePlan, ObjectMapper objectMapper) {
        this.obtainPlan = obtainPlan;
        this.managePlan = managePlan;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<PlanDetailDto> getPlan(String language, String transactionId) {
        return obtainPlan.findByStatusIsTrueAndLanguage(language, transactionId)
                .stream()
                .map(plan -> {
                    double discountPercentage = plan.getDiscount() / 100.0; // Convierte el descuento entero a
                    // porcentaje
                    double discountedPrice = plan.getPrice() * (1 - discountPercentage); // Calcula el precio con
                    // descuento

                    List<String> features = parseFeatures(plan.getFeatures());

                    return new PlanDetailDto(
                            plan.getId(),
                            plan.getName(),
                            plan.getPrice(),
                            plan.getPriceTitle(),
                            plan.getDescription(),
                            discountedPrice,
                            plan.getDiscount(),
                            plan.isStatus(),
                            plan.getCurrency(),
                            plan.getFrequencyType(),
                            plan.getFrequencyValue(),
                            plan.getFreeTrialDays(),
                            plan.getTax(),
                            plan.getReturnTax(),
                            features,
                            plan.getLanguage()
                    );
                })
                .toList();

    }

    @Override
    public List<PlanAdminDto> getPlanAdminList(String transactionId) {
        return obtainPlan.findAll(transactionId).stream()
                .map(this::toPlanAdminDto)
                .toList();
    }

    @Override
    public PlanAdminDto createPlanAdmin(PlanAdminRequestDto request, String transactionId) {
        PlansEntity entity = toEntity(request, null);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        PlansEntity saved = managePlan.save(entity, transactionId);
        return toPlanAdminDto(saved);
    }

    @Override
    public PlanAdminDto updatePlanAdmin(PlanAdminRequestDto request, String transactionId) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        PlansEntity existing = obtainPlan.findById(request.getId(), transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + request.getId()));
        applyRequestToEntity(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        PlansEntity saved = managePlan.save(existing, transactionId);
        return toPlanAdminDto(saved);
    }

    @Override
    public void deletePlanAdmin(Long id, String transactionId) {
        if (!obtainPlan.findById(id, transactionId).isPresent()) {
            throw new IllegalArgumentException("Plan not found: " + id);
        }
        managePlan.deleteById(id, transactionId);
    }

    private PlanAdminDto toPlanAdminDto(PlansEntity plan) {
        int discountPct = plan.getDiscount() != null ? plan.getDiscount() : 0;
        double discountedPrice = plan.getPrice() != null
                ? plan.getPrice() * (1 - discountPct / 100.0)
                : 0.0;
        List<String> features = parseFeatures(plan.getFeatures());
        return new PlanAdminDto(
                plan.getId(),
                plan.getName(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getDiscount(),
                discountedPrice,
                plan.isStatus(),
                plan.getFrequencyType(),
                plan.getFrequencyValue(),
                features
        );
    }

    private PlansEntity toEntity(PlanAdminRequestDto request, PlansEntity existing) {
        PlansEntity entity = existing != null ? existing : new PlansEntity();
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setCurrency(request.getCurrency());
        entity.setDiscount(request.getDiscount() != null ? request.getDiscount() : 0);
        entity.setStatus(request.getStatus() != null ? request.getStatus() : true);
        entity.setFrequencyType(request.getFrequencyType() != null ? request.getFrequencyType() : "MONTH");
        entity.setFrequencyValue(request.getFrequencyValue() != null ? request.getFrequencyValue() : 1);
        if (request.getFeatures() != null) {
            entity.setFeatures(serializeFeatures(request.getFeatures()));
        }
        return entity;
    }

    private String serializeFeatures(List<String> features) {
        if (features == null || features.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(features);
        } catch (Exception e) {
            return "[]";
        }
    }

    private void applyRequestToEntity(PlanAdminRequestDto request, PlansEntity entity) {
        toEntity(request, entity);
    }

    private List<String> parseFeatures(String featuresJson) {
        if (featuresJson == null || featuresJson.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(featuresJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
