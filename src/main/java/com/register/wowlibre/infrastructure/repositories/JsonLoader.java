package com.register.wowlibre.infrastructure.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.register.wowlibre.domain.model.resources.BenefitModel;
import com.register.wowlibre.domain.model.resources.CountryModel;
import com.register.wowlibre.domain.model.resources.PlanModel;
import com.register.wowlibre.domain.model.resources.WidgetHomeSubscriptionModel;
import com.register.wowlibre.domain.port.out.JsonLoaderPort;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JsonLoader implements JsonLoaderPort {
    private final ObjectMapper objectMapper;

    private final Resource jsonFile;
    private final Resource bankPlans;
    private final Resource benefitsGuild;
    private final Resource widgetHomeSubscription;

    private List<CountryModel> jsonCountryModel;
    private Map<String, List<PlanModel>> jsonPlanModel;
    private Map<String, List<BenefitModel>> jsonBenefits;
    private Map<String, List<WidgetHomeSubscriptionModel>> jsonWidgetSubscription;

    public JsonLoader(ObjectMapper objectMapper,
                      @Value("classpath:/static/countryAvailable.json") Resource jsonFile,
                      @Value("classpath:/static/bank_plans.json") Resource bankPlans,
                      @Value("classpath:/static/benefit_guild.json") Resource benefitsGuild,
                      @Value("classpath:/static/subscription_benefit.json") Resource widgetHomeSubscription
    ) {
        this.objectMapper = objectMapper;
        this.jsonFile = jsonFile;
        this.bankPlans = bankPlans;
        this.benefitsGuild = benefitsGuild;
        this.widgetHomeSubscription = widgetHomeSubscription;
    }

    @PostConstruct
    private void loadJsonFile() {
        try {
            jsonCountryModel = readValue(jsonFile.getInputStream(), new TypeReference<>() {
            });
            jsonPlanModel = readValue(bankPlans.getInputStream(), new TypeReference<>() {
            });
            jsonBenefits = readValue(benefitsGuild.getInputStream(), new TypeReference<>() {
            });
            jsonWidgetSubscription = readValue(widgetHomeSubscription.getInputStream(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) throws IOException {
        return objectMapper.readValue(src, valueTypeRef);
    }


    @Override
    public List<CountryModel> getJsonCountry(String transactionId) {
        return jsonCountryModel;
    }


    @Override
    public List<PlanModel> getJsonPlans(String language, String transactionId) {
        return Optional.of(jsonPlanModel.get(language)).orElse(jsonPlanModel.get("es"));
    }

    @Override
    public List<BenefitModel> getJsonBenefitsGuild(String language, String transactionId) {
        return Optional.of(jsonBenefits.get(language)).orElse(jsonBenefits.get("es"));
    }


    @Override
    public WidgetHomeSubscriptionModel getWidgetSubscription(String language, String transactionId) {
        return Optional.of(jsonWidgetSubscription.get(language).stream()
                .findFirst()).orElse(jsonWidgetSubscription.get("es").stream().findFirst()).orElse(null);
    }


}
