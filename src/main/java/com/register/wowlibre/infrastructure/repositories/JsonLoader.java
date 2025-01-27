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
    private final Resource serversPromos;
    private final Resource bannersHome;
    private final Resource widgetHomeSubscription;
    private final Resource promotions;
    private final Resource faqsSubscriptionJsonFile;
    private final Resource serverExperience;

    private List<CountryModel> jsonCountryModel;
    private Map<String, List<FaqsModel>> jsonFaqsModel;
    private Map<String, List<PlanModel>> jsonPlanModel;
    private Map<String, List<BenefitModel>> jsonBenefits;
    private Map<String, List<ServersPromotions>> jsonServerPromos;
    private Map<String, List<BannerHomeModel>> jsonBannerHome;
    private Map<String, List<WidgetHomeSubscriptionModel>> jsonWidgetSubscription;
    private Map<String, List<PromotionModel>> jsonPromotions;
    private Map<String, List<FaqsModel>> jsonFaqsSubscriptionModel;
    private Map<String, List<ExperiencesHomeModel>> jsonServerExperienceModel;

    public JsonLoader(ObjectMapper objectMapper,
                      @Value("classpath:/static/countryAvailable.json") Resource jsonFile,
                      @Value("classpath:/static/faqs.json") Resource faqsJsonFile,
                      @Value("classpath:/static/bank_plans.json") Resource bankPlans,
                      @Value("classpath:/static/benefit_guild.json") Resource benefitsGuild,
                      @Value("classpath:/static/servers_promotions.json") Resource serverPromos,
                      @Value("classpath:/static/banner_home.json") Resource bannersHome,
                      @Value("classpath:/static/subscription_benefit.json") Resource widgetHomeSubscription,
                      @Value("classpath:/static/promotions.json") Resource promotions,
                      @Value("classpath:/static/faqs_subscriptions.json") Resource faqsSubscriptionJsonFile,
                      @Value("classpath:/static/server_experiences.json") Resource serverExperience) {
        this.objectMapper = objectMapper;
        this.jsonFile = jsonFile;
        this.faqsJsonFile = faqsJsonFile;
        this.bankPlans = bankPlans;
        this.benefitsGuild = benefitsGuild;
        this.serversPromos = serverPromos;
        this.bannersHome = bannersHome;
        this.widgetHomeSubscription = widgetHomeSubscription;
        this.promotions = promotions;
        this.faqsSubscriptionJsonFile = faqsSubscriptionJsonFile;
        this.serverExperience = serverExperience;

    }

    @PostConstruct
    public void loadJsonFile() {
        try {
            jsonCountryModel = objectMapper.readValue(jsonFile.getInputStream(), new TypeReference<>() {
            });
            jsonFaqsModel = objectMapper.readValue(faqsJsonFile.getInputStream(), new TypeReference<>() {
            });
            jsonFaqsSubscriptionModel = objectMapper.readValue(faqsSubscriptionJsonFile.getInputStream(),
                    new TypeReference<>() {
                    });
            jsonPlanModel = objectMapper.readValue(bankPlans.getInputStream(), new TypeReference<>() {
            });
            jsonBenefits = objectMapper.readValue(benefitsGuild.getInputStream(), new TypeReference<>() {
            });
            jsonServerPromos = objectMapper.readValue(serversPromos.getInputStream(), new TypeReference<>() {
            });
            jsonBannerHome = objectMapper.readValue(bannersHome.getInputStream(), new TypeReference<>() {
            });
            jsonWidgetSubscription = objectMapper.readValue(widgetHomeSubscription.getInputStream(),
                    new TypeReference<>() {
                    });
            jsonPromotions = objectMapper.readValue(promotions.getInputStream(),
                    new TypeReference<>() {
                    });
            jsonServerExperienceModel = objectMapper.readValue(serverExperience.getInputStream(),
                    new TypeReference<>() {
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
    public List<FaqsModel> getJsonFaqs(String language, String transactionId) {
        return Optional.of(jsonFaqsModel.get(language)).orElse(jsonFaqsModel.get("es"));

    }

    @Override
    public List<FaqsModel> getJsonFaqsSubscriptions(String language, String transactionId) {
        return Optional.of(jsonFaqsSubscriptionModel.get(language)).orElse(jsonFaqsSubscriptionModel.get("es"));
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
    public List<ServersPromotions> getJsonServersPromoGuild(String language, String transactionId) {
        return Optional.of(jsonServerPromos.get(language)).orElse(jsonServerPromos.get("es"));
    }

    @Override
    public List<BannerHomeModel> getBannersHome(String language, String transactionId) {
        return Optional.of(jsonBannerHome.get(language)).orElse(jsonBannerHome.get("es"));
    }

    @Override
    public WidgetHomeSubscriptionModel getWidgetSubscription(String language, String transactionId) {
        return Optional.of(jsonWidgetSubscription.get(language).stream()
                .findFirst()).orElse(jsonWidgetSubscription.get("es").stream().findFirst()).orElse(null);
    }

    @Override
    public List<PromotionModel> getPromotions(String language, String transactionId) {
        return Optional.of(jsonPromotions.get(language)).orElse(jsonPromotions.get("es"));
    }

    @Override
    public List<ExperiencesHomeModel> getExperiencesHome(String language, String transactionId) {
        return Optional.of(jsonServerExperienceModel.get(language)).orElse(jsonServerExperienceModel.get("es"));
    }


}
