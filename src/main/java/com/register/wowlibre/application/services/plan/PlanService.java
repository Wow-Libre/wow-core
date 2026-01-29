package com.register.wowlibre.application.services.plan;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.plan.*;
import com.register.wowlibre.domain.port.out.plans.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class PlanService implements PlanPort {

    private final ObtainPlan obtainPlan;
    private final ObjectMapper objectMapper;

    public PlanService(ObtainPlan obtainPlan, ObjectMapper objectMapper) {
        this.obtainPlan = obtainPlan;
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
