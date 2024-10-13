package com.register.wowlibre.infrastructure.repositories;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.out.*;
import jakarta.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Component
public class JsonLoader implements JsonLoaderPort {
    private final ObjectMapper objectMapper;

    private final Resource jsonFile;
    private final Resource faqsJsonFile;
    private final Resource bankPlans;
    private final Resource benefitsGuild;

    private List<CountryModel> jsonCountryModel;
    private List<FaqsModel> jsonFaqsModel;
    private Map<String, List<PlanModel>> jsonPlanModel;

    private Map<String, List<BenefitModel>> jsonBenefits;

    public JsonLoader(ObjectMapper objectMapper,
                      @Value("classpath:/static/countryAvailable.json") Resource jsonFile,
                      @Value("classpath:/static/faqs.json") Resource faqsJsonFile,
                      @Value("classpath:/static/bank_plans.json") Resource bankPlans,
                      @Value("classpath:/static/benefit_guild.json") Resource benefitsGuild) {
        this.objectMapper = objectMapper;
        this.jsonFile = jsonFile;
        this.faqsJsonFile = faqsJsonFile;
        this.bankPlans = bankPlans;
        this.benefitsGuild = benefitsGuild;
    }

    @PostConstruct
    public void loadJsonFile() {
        try {
            jsonCountryModel = objectMapper.readValue(jsonFile.getInputStream(), new TypeReference<>() {
            });
            jsonFaqsModel = objectMapper.readValue(faqsJsonFile.getInputStream(), new TypeReference<>() {
            });
            jsonPlanModel = objectMapper.readValue(bankPlans.getInputStream(), new TypeReference<>() {
            });
            jsonBenefits = objectMapper.readValue(benefitsGuild.getInputStream(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CountryModel> getJsonCountry(String transactionId) {
        return jsonCountryModel;
    }

    @Override
    public List<FaqsModel> getJsonFaqs(String transactionId) {
        return jsonFaqsModel;
    }

    @Override
    public List<PlanModel> getJsonPlans(String language, String transactionId) {
        return Optional.of(jsonPlanModel.get(language)).orElse(jsonPlanModel.get("es"));
    }

    @Override
    public List<BenefitModel> getJsonBenefitsGuild(String language, String transactionId) {
        return Optional.of(jsonBenefits.get(language)).orElse(jsonBenefits.get("es"));
    }


}
