package com.register.wowlibre.application.services.resources;


import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.domain.port.out.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class ResourcesService implements ResourcesPort {
    private final JsonLoaderPort jsonLoaderPort;

    public ResourcesService(JsonLoaderPort jsonLoaderPort) {
        this.jsonLoaderPort = jsonLoaderPort;
    }

    @Override
    public List<CountryModel> getCountry(String transactionId) {
        return jsonLoaderPort.getJsonCountry(transactionId);
    }

    @Override
    public List<FaqsModel> getFaqs(String transactionId) {
        return jsonLoaderPort.getJsonFaqs(transactionId);
    }

    @Override
    public List<PlanModel> getPlansBank(String language, String transactionId) {
        return jsonLoaderPort.getJsonPlans(language, transactionId);
    }

    @Override
    public List<BenefitModel> getBenefitsGuild(String language, String transactionId) {
        return jsonLoaderPort.getJsonBenefitsGuild(language, transactionId);
    }

    @Override
    public List<ServersPromotions> getJsonServersPromoGuild(String language, String transactionId) {
        System.out.println(language);
        return jsonLoaderPort.getJsonServersPromoGuild(language, transactionId);
    }

}
